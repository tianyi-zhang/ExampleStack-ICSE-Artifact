public class foo{
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            Object obj = node.getUserObject();          
            TreePath tp = new TreePath(node.getPath());
            altLabel.setText(obj != null ? obj.toString() : "");
            altLabel.setForeground(UIManager.getColor(selected ? "Tree.selectionForeground" : "Tree.textForeground"));
            CheckedNode cn = nodesCheckingState.get(tp);
            if (cn == null) {
                checkBox.setVisible(false);
                return this;
            }
            if (cn.isCheckBoxEnabled) {
	            checkBox.setSelected(cn.isSelected);
	            checkBox.setOpaque(cn.isSelected && cn.hasChildren && ! cn.allChildrenSelected);
	        	checkBox.setVisible(true);
	        	checkBox.setEnabled(true);
	        	/* Looks ok, but doesnt work correctly
	            if (cn.isSelected && cn.hasChildren && ! cn.allChildrenSelected) {
	                checkBox.getModel().setPressed(true);
	                checkBox.getModel().setArmed(true);
	            } else {
	                checkBox.getModel().setPressed(false);
	                checkBox.getModel().setArmed(false);
	            }
	            */
            } else {
            	checkBox.setVisible(false);
            	checkBox.setEnabled(false);
            }

            return this;
        }       
}