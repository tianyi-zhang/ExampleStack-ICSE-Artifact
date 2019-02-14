public class foo{
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            if (t == null && r instanceof Future<?>) {
                try {
                    Future<?> future = (Future<?>) r;
                    if (future.isDone()) {
                        future.get();
                    }
                } catch (CancellationException ce) {
                    t = ce;
                } catch (ExecutionException ee) {
                    t = ee.getCause();
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt(); // ignore/reset
                }
            }
            if (t != null) {
                LOG.error("Uncaught Exception: ", t);
                LOG.error("stacktrace: " + ExceptionUtils.getStackTrace(t).replaceAll("\n", " ").replaceAll("\\s+", " "));
            }
        }
}