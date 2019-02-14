public class foo {
        @Override
        public Void loadInBackground() {
            for (int i = 0; i < MAX_COUNT; i++) {
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(getClass().getSimpleName(), "Progress value is " + i);
                Log.d(getClass().getSimpleName(), "getActivity is " + getContext());
                Log.d(getClass().getSimpleName(), "this is " + this);

                final int progress = i;
                if (mActivity.get() != null) {
                    mActivity.get().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mActivity.get().progressBar.setProgress(progress);
                        }
                    });
                }
            }
            return null;
        }
}