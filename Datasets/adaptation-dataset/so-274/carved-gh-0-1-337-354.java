public class foo{
    private void updatePredecessorsAllChildrenSelectedState(TreePath tp) {
        TreePath parentPath = tp.getParentPath();
        if (parentPath == null) {
            return;
        }
        CheckedNode parentCheckedNode = nodesCheckingState.get(parentPath);
        parentCheckedNode.allChildrenSelected = true;
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
        for (int i = 0; i < parentNode.getChildCount(); i++) {
            TreePath childPath = parentPath.pathByAddingChild(parentNode.getChildAt(i));
            CheckedNode childCheckedNode = nodesCheckingState.get(childPath);
            if (!allSelected(childCheckedNode)) {
                parentCheckedNode.allChildrenSelected = false;
                break;
            }
        }
        updatePredecessorsAllChildrenSelectedState(parentPath);
    }
}