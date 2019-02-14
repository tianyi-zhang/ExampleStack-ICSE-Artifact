public class foo {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected, boolean expanded, boolean leaf, int row,
                                                      boolean hasFocus) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            Object obj = node.getUserObject();

            checkBox.setText(obj.toString());

            if (obj instanceof Boolean)
                checkBox.setText("Retrieving data...");
            else
            {
                TreePath tp = new TreePath(node.getPath());
                JCheckBoxTree.CheckedNode cn = null;
                if( cbt != null )
                    cn = cbt.getCheckedNode(tp);
                if (cn == null) {
                    return this;
                }
                checkBox.setSelected(cn.isSelected);
                checkBox.setText(obj.toString());
                checkBox.setOpaque(cn.isSelected && cn.hasChildren && ! cn.allChildrenSelected);
            }
            return this;
        }
}