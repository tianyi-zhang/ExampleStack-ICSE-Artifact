public class foo {
  public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
    mListener = listener;
    mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null && mListener != null) {
                mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
            }
        }
    });
}
}