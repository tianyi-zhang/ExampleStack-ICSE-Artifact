public class foo{
    private void ensureList() {
        if (mList != null) {
            return;
        }
        View root = getView();
        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        if (root instanceof ExpandableListView) {
            mList = (ExpandableListView) root;
        } else {
            mStandardEmptyView = (TextView) root.findViewById(INTERNAL_EMPTY_ID);
            if (mStandardEmptyView == null) {
                mEmptyView = root.findViewById(android.R.id.empty);
            }
            mListContainer = null; //root.findViewById(com.android.internal.R.id.listContainer);
            View rawListView = root.findViewById(android.R.id.list);
            if (!(rawListView instanceof ExpandableListView)) {
                if (rawListView == null) {
                    throw new RuntimeException("Your content must have a ExpandableListView whose id attribute is "
                            + "'android.R.id.list'");
                }
                throw new RuntimeException(
                        "Content has view with id attribute 'android.R.id.list' "
                                + "that is not a ExpandableListView class");
            }
            mList = (ExpandableListView) rawListView;
            if (mEmptyView != null) {
                mList.setEmptyView(mEmptyView);
            }
        }
        mListShown = true;
        mList.setOnItemClickListener(mOnClickListener);
        mList.setOnChildClickListener(mOnChildClickListener);
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