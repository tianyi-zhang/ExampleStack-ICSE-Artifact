public class foo{
    /**
     * Attempt to determine if a socket is in use.
     *
     * @param port
     * @return
     */
    private static boolean available(int port)
    {
        Socket s = null;
        try
        {
            s = new Socket("localhost", port);
            s.setReuseAddress(true);

            // If the code makes it this far without an exception it means
            // something is using the port and has responded.
            getLogger().debug("Port " + port + " is not available");
            return false;
        }
        catch (IOException e)
        {
            getLogger().debug("Port " + port + " is available");
            return true;
        }
        finally
        {
            if (s != null)
            {
                try
                {
                    s.close();
                }
                catch (IOException e)
                {
                    throw new RuntimeException("Failed to close the socket", e);
                }
            }
        }
    }
}