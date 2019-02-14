public class foo{
		/**
		 * Overriding this method makes sure that exceptions thrown by a task are not silently swallowed.
		 * <p/>
		 * Thanks to nos for this solution: http://stackoverflow.com/a/2248203/1125055
		 */
		@Override
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
				logger.warn("Error while executing task in " + this, t);
			}
		}
}