public class foo{
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
}