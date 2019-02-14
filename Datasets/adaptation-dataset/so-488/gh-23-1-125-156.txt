package is.gangverk.remoteconfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Pair;

public class Utils {

	/**
	 * Checks if there is wifi or mobile connection available 
	 * @param context The application context
	 * @return true if there is network connection available
	 */
	public static boolean isNetworkConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null && activeNetwork.isConnected();
	}
	
	/**
	 * Reads a stream and writes it into a string. Closes inputStream when done.
	 * @param inputStream The stream to read
	 * @return A string, containing stream data
	 * @throws java.io.IOException
	 */
	public static String stringFromStream(InputStream inputStream) throws java.io.IOException{
		String encoding = "UTF-8";
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, encoding));
		String line;
		while((line = reader.readLine()) != null) {
			builder.append(line);
		}
		reader.close();
		return builder.toString();
	}

	private static final int HTTP_CONNECTION_TIMEOUT = 80000;
	private static final int HTTP_SOCKET_TIMEOUT = 100000;

	/**
	 * Get the default http params (to prevent infinite http hangs)
	 * @return reasonable default for a HttpClient
	 */
	public static void setHttpTimeoutParams(DefaultHttpClient httpClient) {
		HttpParams httpParameters = new BasicHttpParams();
		// connection established timeout
		HttpConnectionParams.setConnectionTimeout(httpParameters, HTTP_CONNECTION_TIMEOUT);
		// socket timeout
		HttpConnectionParams.setSoTimeout(httpParameters, HTTP_SOCKET_TIMEOUT);
		httpClient.setParams(httpParameters);
	}

	public static String readJSONFeedString(String urlString, ArrayList<Pair<String, String>> headers) {
		if(urlString==null)
			return null;
		String stringResponse = null;
		DefaultHttpClient httpClient = getDefaultHttpClient();
		Utils.setHttpTimeoutParams(httpClient);
		try {
			HttpRequestBase httpRequest = new HttpGet();
			httpRequest.addHeader("Accept", "application/json");
			if(headers != null) {
				for(int i = 0; i<headers.size() ;i++) {
					httpRequest.addHeader(headers.get(i).first, headers.get(i).second);
				}
			}
			httpRequest.setURI(URI.create(urlString));
			HttpResponse response = httpClient.execute(httpRequest);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if(statusCode==200) {
				HttpEntity entity = response.getEntity();
				stringResponse = Utils.stringFromStream(entity.getContent());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return stringResponse;
	}

	/**
	 * Returns an httpClient that doesn't verify ssl hostname. Only needs to be used
	 * in android <2.2 phones, otherwise DefaultHttpClient should be used
	 * @return
	 */
	public static DefaultHttpClient getDefaultHttpClient() {

		if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) { // 10 = Android 2.3.3
			return new DefaultHttpClient();
		}
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	/**
	 *  Avoid ssl exception on some phones
	 * see: http://stackoverflow.com/questions/2642777/trusting-all-certificates-using-httpclient-over-https
	 *
	 */
	private static class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	public static JSONObject readJSONFeed(String urlString, ArrayList<Pair<String, String>> headers) {
		try {
			String jsonString = readJSONFeedString(urlString, headers);
			if(jsonString==null) return null;
			return new JSONObject(jsonString);
		} catch (JSONException e) {                        
			e.printStackTrace();
		}
		return null;
	}

}
