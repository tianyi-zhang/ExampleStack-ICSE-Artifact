public class foo {
    private void ensureList() {
        if (mExpandableList != null) {
            return;
        }
        View root = getView();
        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        if (root instanceof ExpandableListView) {
            mExpandableList = (ExpandableListView)root;
        } else {
            mStandardEmptyView = (TextView)root.findViewById(INTERNAL_EMPTY_ID);
            if (mStandardEmptyView == null) {
                mEmptyView = root.findViewById(android.R.id.empty);
            } else {
                mStandardEmptyView.setVisibility(View.GONE);
            }
            mProgressContainer = root.findViewById(INTERNAL_PROGRESS_CONTAINER_ID);
            mExpandableListContainer = root.findViewById(INTERNAL_LIST_CONTAINER_ID);
            View rawExpandableListView = root.findViewById(android.R.id.list);
            if (!(rawExpandableListView instanceof ExpandableListView)) {
                if (rawExpandableListView == null) {
                    throw new RuntimeException(
                            "Your content must have a ListView whose id attribute is " +
                            "'android.R.id.list'");
                }
                throw new RuntimeException(
                        "Content has view with id attribute 'android.R.id.list' "
                        + "that is not a ListView class");
            }
            mExpandableList = (ExpandableListView)rawExpandableListView;
            if (mEmptyView != null) {
                mExpandableList.setEmptyView(mEmptyView);
            } else if (mEmptyText != null) {
                mStandardEmptyView.setText(mEmptyText);
                mExpandableList.setEmptyView(mStandardEmptyView);
            }
        }
        mExpandableListShown = true;
        mExpandableList.setOnItemClickListener(mOnClickListener);
        if (mAdapter != null) {
            ExpandableListAdapter adapter = mAdapter;
            mAdapter = null;
            setListAdapter(adapter);
        } else {
            // We are starting without an adapter, so assume we won't
            // have our data right away and start with the progress indicator.
            if (mProgressContainer != null) {
                setListShown(false, false);
            }
        }
        mHandler.post(mRequestFocus);
    }
}