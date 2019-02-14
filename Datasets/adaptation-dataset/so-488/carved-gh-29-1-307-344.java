public class foo{
    // taken from http://stackoverflow.com/questions/2642777/trusting-all-certificates-using-httpclient-over-https
    private DefaultHttpClient getNewHttpClient() {
        if (this.acceptAllSslCertificates) {
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);

                SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                HttpParams params = new BasicHttpParams();
                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

                // Set the timeout in milliseconds until a connection is established.
                // The default value is zero, that means the timeout is not used.
                int timeoutConnection = 15000;
                HttpConnectionParams.setConnectionTimeout(params, timeoutConnection);

                // Set the default socket timeout (SO_TIMEOUT)
                // in milliseconds which is the timeout for waiting for data.
                int timeoutSocket = 45000;
                HttpConnectionParams.setSoTimeout(params, timeoutSocket);

                SchemeRegistry registry = new SchemeRegistry();
                registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), this.port));
                registry.register(new Scheme("https", sf, this.sslPort));

                ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

                return new DefaultHttpClient(ccm, params);
            } catch (Exception e) {
                return new DefaultHttpClient();
            }
        } else {
            return new DefaultHttpClient();
        }
    }
}