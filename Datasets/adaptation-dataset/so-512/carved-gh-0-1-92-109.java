public class foo{
        @Override
        public Void loadInBackground() {
            for ( int progress = 0; progress < MAX_COUNT && !isReset(); progress++ ) {
                try {
                    Thread.sleep( SLEEP_TIME );
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
                Log.d( getClass().getSimpleName(), "Progress value is " + progress );
                Log.d( getClass().getSimpleName(), "getActivity is " + getContext() );
                Log.d( getClass().getSimpleName(), "this is " + this );

                if ( mActivity.get() != null ) {
                    mActivity.get().updateProgress( progress );
                }
            }
            return null;
        }
}