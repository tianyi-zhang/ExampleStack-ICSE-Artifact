package com.cantinasoftware.restclient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class SelfSignedCertificatesHelpers {

	public static class FakeSocketFactory implements SocketFactory,
			LayeredSocketFactory {
		private String certKey = null;
		private SSLContext sslcontext = null;

		public FakeSocketFactory(String certKey) {
			this.certKey = certKey;
		}

		public FakeSocketFactory() {
		}

		private static SSLContext createEasySSLContext(String certKey)
				throws IOException {
			try {
				SSLContext context = SSLContext.getInstance("TLS");
				context.init(null, new TrustManager[] { new FakeTrustManager(
						certKey) }, null);
				return context;
			} catch (Exception e) {
				throw new IOException(e.getMessage());
			}
		}

		private SSLContext getSSLContext() throws IOException {
			if (this.sslcontext == null) {
				this.sslcontext = createEasySSLContext(this.certKey);
			}
			return this.sslcontext;
		}

		@Override
		public Socket connectSocket(Socket sock, String host, int port,
				InetAddress localAddress, int localPort, HttpParams params)
				throws IOException, UnknownHostException,
				ConnectTimeoutException {
			int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
			int soTimeout = HttpConnectionParams.getSoTimeout(params);

			InetSocketAddress remoteAddress = new InetSocketAddress(host, port);
			SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock
					: createSocket());

			if ((localAddress != null) || (localPort > 0)) {
				// we need to bind explicitly
				if (localPort < 0) {
					localPort = 0; // indicates "any"
				}
				InetSocketAddress isa = new InetSocketAddress(localAddress,
						localPort);
				sslsock.bind(isa);
			}

			sslsock.connect(remoteAddress, connTimeout);
			sslsock.setSoTimeout(soTimeout);
			return sslsock;
		}

		@Override
		public Socket createSocket() throws IOException {
			return getSSLContext().getSocketFactory().createSocket();
		}

		@Override
		public boolean isSecure(Socket arg0) throws IllegalArgumentException {
			return true;
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return getSSLContext().getSocketFactory().createSocket(socket,
					host, port, autoClose);
		}

	}

	public static class FakeTrustManager implements X509TrustManager {
		private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};
		private String certKey = null;

		FakeTrustManager(String certKey) {
			super();
			this.certKey = certKey;
		}

		FakeTrustManager() {
			super();
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			if (this.certKey == null) {
				// This is the Accept All certificates case.
				return;
			}

			// Otherwise, we have a certKey defined. We should now examine the
			// one we got from the server.
			// They match? All is good. They don't, throw an exception.
			String our_key = this.certKey.replaceAll("[^a-f0-9]+", "");
			try {
				// Assume self-signed root is okay?
				X509Certificate ss_cert = chain[0];
				String thumbprint = getThumbPrint(ss_cert);
				if (our_key.equalsIgnoreCase(thumbprint)) {
					return;
				} else {
					throw new CertificateException("Certificate key ["
							+ thumbprint + "] doesn't match expected value.");
				}
			} catch (NoSuchAlgorithmException e) {
				throw new CertificateException(
						"Unable to check self-signed cert, unknown algorithm. "
								+ e.toString());
			}

		}

		public boolean isClientTrusted(X509Certificate[] chain) {
			return true;
		}

		public boolean isServerTrusted(X509Certificate[] chain) {
			return true;
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return _AcceptedIssuers;
		}

		// Thank you:
		// http://stackoverflow.com/questions/1270703/how-to-retrieve-compute-an-x509-certificates-thumbprint-in-java
		private static String getThumbPrint(X509Certificate cert)
				throws NoSuchAlgorithmException, CertificateEncodingException {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] der = cert.getEncoded();
			md.update(der);
			byte[] digest = md.digest();
			return hexify(digest);
		}

		private static String hexify(byte bytes[]) {
			char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
					'9', 'a', 'b', 'c', 'd', 'e', 'f' };

			StringBuffer buf = new StringBuffer(bytes.length * 2);

			for (int i = 0; i < bytes.length; ++i) {
				buf.append(hexDigits[(bytes[i] & 0xf0) >> 4]);
				buf.append(hexDigits[bytes[i] & 0x0f]);
			}

			return buf.toString();
		}

	}
}
