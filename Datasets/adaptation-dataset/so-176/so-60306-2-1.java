public class foo {
  public static void main(String[] args) {
    JFrame test = new JFrame("Tab test");
    test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    test.setSize(400, 400);

    DraggableTabbedPane tabs = new DraggableTabbedPane();
    tabs.addTab("One", new JButton("One"));
    tabs.addTab("Two", new JButton("Two"));
    tabs.addTab("Three", new JButton("Three"));
    tabs.addTab("Four", new JButton("Four"));

    test.add(tabs);
    test.setVisible(true);
  }
}