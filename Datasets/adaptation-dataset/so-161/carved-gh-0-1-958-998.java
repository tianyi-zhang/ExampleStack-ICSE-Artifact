public class foo{
    /**
     * List needs special handling, see
     * http://stackoverflow.com/questions/7306295/swing-jlist-with-multiline-text-and-dynamic-height
     * 
     */
    public void interactiveTextAreaRendererList() {
        final DefaultListModel model = new DefaultListModel();
        model.addElement("Start: This is a short text");
        model.addElement("Start: This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. ");
        model.addElement("Start: This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. ");

        final JXList list = new JXList(model) {

            /** 
             * @inherited <p>
             */
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return true;
            }

            
        };
        list.setCellRenderer(new DefaultListRenderer(new TextAreaProvider()));
        
        ComponentListener l = new java.awt.event.ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                list.invalidateCellSizeCache();
                // for core: force cache invalidation by temporarily setting fixed height
//                list.setFixedCellHeight(10);
//                list.setFixedCellHeight(-1);
            }
            
        };

        list.addComponentListener(l);
        showWithScrollingInFrame(list, "TextAreaPovider in JXList");
        
    }
}