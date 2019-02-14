public class foo{
    private void ensureList() {
        if (mList != null) return;

        View root = getView();
        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }

        if (root instanceof ExpandableListView) {
            mList = (ExpandableListView) root;
        } else {
            mEmptyViewScroll = (ScrollView) root.findViewById(R.id.emptyViewScroll);
//            mStandardEmptyView = (TextView) root.findViewById(R.id.empty);
            mStandardEmptyView = (TextView) root.findViewById(android.R.id.empty);
            if (mStandardEmptyView == null) {
                mEmptyView = root.findViewById(android.R.id.empty);
            } else {
                mEmptyViewScroll.setVisibility(View.GONE);
            }

            mListContainer = root.findViewById(R.id.container);
            View rawListView = root.findViewById(android.R.id.list);
            if (!(rawListView instanceof ExpandableListView)) {
                if (rawListView == null) {
                    throw new RuntimeException("Your content must have a ExpandableListView whose id attribute is " +
                            "'android.R.id.list'");
                }
                throw new RuntimeException("Content has view with id attribute 'android.R.id.list' " +
                        "that is not a ExpandableListView class");
            }

            mList = (ExpandableListView) rawListView;
            if (mEmptyView != null) {
//                mList.setEmptyView(mEmptyView);
                mList.setEmptyView(mEmptyViewScroll);
            } else if (mEmptyTextSet) {
                mStandardEmptyView.setText(mEmptyText);
                mList.setEmptyView(mEmptyViewScroll);
            }
        }

        mListShown = true;
        mList.setOnItemClickListener(mOnClickListener);
        if (mAdapter != null) {
            setListAdapter(mAdapter);
        } else {
            // We are starting without an adapter, so assume we won't
            // have our data right away and start with the progress indicator.
            setListShown(false, false);
        }
        mHandler.post(mRequestFocus);
    }
}