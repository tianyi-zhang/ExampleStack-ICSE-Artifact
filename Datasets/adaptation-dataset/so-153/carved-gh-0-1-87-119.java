public class foo{
    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);

        // If you tap on the phone while the RecyclerView is scrolling it will stop in the middle.
        // This code fixes this. This code is not strictly necessary but it improves the behaviour.

        if (state == SCROLL_STATE_IDLE) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();

//            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            int screenWidth = getMeasuredWidth();
            // views on the screen
            int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            View lastView = linearLayoutManager.findViewByPosition(lastVisibleItemPosition);
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            View firstView = linearLayoutManager.findViewByPosition(firstVisibleItemPosition);

            // distance we need to scroll
            int leftMargin = (screenWidth - lastView.getWidth()) / 2;
            int rightMargin = (screenWidth - firstView.getWidth()) / 2 + firstView.getWidth();
            int leftEdge = lastView.getLeft();
            int rightEdge = firstView.getRight();
            int scrollDistanceLeft = leftEdge - leftMargin;
            int scrollDistanceRight = rightMargin - rightEdge;

            if (leftEdge > screenWidth / 2) {
                smoothScrollBy(-scrollDistanceRight, 0);
            } else if (rightEdge < screenWidth / 2) {
                smoothScrollBy(scrollDistanceLeft, 0);
            }
        }
    }
}