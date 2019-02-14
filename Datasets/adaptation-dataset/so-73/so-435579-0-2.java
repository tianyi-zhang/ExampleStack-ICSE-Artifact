public class foo {
/**
 * Checks to see if a specific port is available.
 *
 * @param port the port to check for availability
 */
public static boolean available(int port) {
    if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
        throw new IllegalArgumentException("Invalid start port: " + port);
    }

    ServerSocket ss = null;
    DatagramSocket ds = null;
    try {
        ss = new ServerSocket(port);
        ss.setReuseAddress(true);
        ds = new DatagramSocket(port);
        ds.setReuseAddress(true);
        return true;
    } catch (IOException e) {
    } finally {
        if (ds != null) {
            ds.close();
        }

        if (ss != null) {
            try {
                ss.close();
            } catch (IOException e) {
                /* should not be thrown */
            }
        }
    }

    return false;
}
}