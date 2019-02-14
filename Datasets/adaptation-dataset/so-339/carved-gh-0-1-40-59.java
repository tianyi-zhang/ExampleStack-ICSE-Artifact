public class foo{
    /**
     * Call only on OS X
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void setOpenURIAppleEventHandler(OpenUriAppleEventHandler urlHandler) {
        try {
            Class applicationClass = Class.forName("com.apple.eawt.Application");
            Method getApplicationMethod = applicationClass.getDeclaredMethod("getApplication", (Class[])null);
            Object application = getApplicationMethod.invoke(null, (Object[])null);

            Class openURIHandlerClass = Class.forName("com.apple.eawt.OpenURIHandler", false, applicationClass.getClassLoader());
            Method setOpenURIHandlerMethod = applicationClass.getMethod("setOpenURIHandler", openURIHandlerClass);

            OpenURIEventInvocationHandler handler = new OpenURIEventInvocationHandler(urlHandler);
            Object openURIEvent = Proxy.newProxyInstance(openURIHandlerClass.getClassLoader(), new Class[] { openURIHandlerClass }, handler);
            setOpenURIHandlerMethod.invoke(application, openURIEvent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}