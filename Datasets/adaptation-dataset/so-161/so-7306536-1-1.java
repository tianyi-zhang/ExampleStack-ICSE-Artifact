public class foo {
    private MultiLineList()
    {
        JFrame f = new JFrame("MultiLineList");
        f.setResizable(true);
        f.setVisible(true);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.getContentPane().setLayout(new BorderLayout());

        final DefaultListModel model = new DefaultListModel();
        model.addElement("This is a short text");
        model.addElement("This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. This is a long text. ");
        model.addElement("This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. This is an even longer text. ");

        final JList list = new JList(model);
        list.setCellRenderer(new MyCellRenderer());

        f.add(list);

        f.pack();
    }
}