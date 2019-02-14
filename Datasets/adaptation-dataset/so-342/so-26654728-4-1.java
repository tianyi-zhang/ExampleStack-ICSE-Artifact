public class foo {
public static void closeSilently(Object... xs) {
    // Note: on Android API levels prior to 19 Socket does not implement Closeable
    for (Object x : xs) {
        if (x != null) {
            try {
                Log.d("closing: "+x);
                if (x instanceof Closeable) {
                    ((Closeable)x).close();
                } else if (x instanceof Socket) {
                    ((Socket)x).close();
                } else if (x instanceof DatagramSocket) {
                    ((DatagramSocket)x).close();
                } else {
                    Log.d("cannot close: "+x);
                    throw new RuntimeException("cannot close "+x);
                }
            } catch (Throwable e) {
                Log.x(e);
            }
        }
    }
}
}