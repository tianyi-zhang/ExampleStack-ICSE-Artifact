public class foo{
	/**
	 * Switches validation of SSL certificates on or off by installing a default
	 * all-trusting {@link TrustManager}.
	 * @param enabled whether to validate SSL certificates
	 * @author neu242 (http://stackoverflow.com/a/876785)
	 */
	public static void setSSLCertValidation(boolean enabled) {
		// create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[]{
			new X509TrustManager() {
				@Override public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
				@Override public void checkClientTrusted(X509Certificate[] certs, String authType) {}
				@Override public void checkServerTrusted(X509Certificate[] certs, String authType) {}
			}
		};

		// install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, enabled ? null : trustAllCerts, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {}
	}
}