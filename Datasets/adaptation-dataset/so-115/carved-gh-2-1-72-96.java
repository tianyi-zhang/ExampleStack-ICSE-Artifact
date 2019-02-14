public class foo{
    /**
     * Handles the uncaught exception
     * @param t
     * @param e
     */
    public void uncaughtException(Thread t, Throwable e) {
        Long tsLong = System.currentTimeMillis();
        String timestamp = tsLong.toString();
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();
        String filename = timestamp + "." + STACKTRACE_EXT;

        if (mStracktraceDir != null) {
            writeToFile(stacktrace, filename);
        }

        defaultUEH.uncaughtException(t, e);

        // force shut down so we don't end up with un-initialized objects
        Process.killProcess(Process.myPid());
        System.exit(0);
    }
}