public class foo{
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            Object obj = node.getUserObject();          
            TreePath tp = new TreePath(node.getPath());
            CheckedNode cn = nodesCheckingState.get(tp);
            if (cn == null) {
                return this;
            }
            checkBox.setSelected(cn.isSelected);
            if(obj instanceof FEntry){
            	FEntry f = (FEntry) obj;
            	 checkBox.setText(f.getFileName());
            }else{
            	 checkBox.setText(obj.toString());
            }
           
            checkBox.setOpaque(cn.isSelected && cn.hasChildren && ! cn.allChildrenSelected);
            return this;
        }       
}