public class foo {
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        AuthState authState = (AuthState) context.getAttribute(HttpClientContext.TARGET_AUTH_STATE);
        // If no auth scheme available yet, try to initialize it
        // preemptively
        if (authState.getAuthScheme() == null) {
            CredentialsProvider credsProvider = (CredentialsProvider) 
                        context.getAttribute(HttpClientContext.CREDS_PROVIDER);
            HttpHost targetHost = (HttpHost) context.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
            AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
            Credentials creds = credsProvider.getCredentials(authScope);
            if(creds == null){

            }
            authState.update(new BasicScheme(), creds);
        }

    }
}