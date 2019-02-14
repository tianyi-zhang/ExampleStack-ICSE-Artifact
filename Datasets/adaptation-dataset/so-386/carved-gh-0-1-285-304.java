public class foo{
			public TestFrame() {
				super();
				setSize(500, 500);
				this.getContentPane().setLayout(new BorderLayout());
				final JCheckBoxTree cbt = new JCheckBoxTree();
				this.getContentPane().add(cbt);
				cbt.addCheckChangeEventListener(new JCheckBoxTree.CheckChangeEventListener() {
					public void checkStateChanged(JCheckBoxTree.CheckChangeEvent event) {
						System.out.println("event");
						TreePath[] paths = cbt.getCheckedPaths();
						for (TreePath tp : paths) {
							for (Object pathPart : tp.getPath()) {
								System.out.print(pathPart + ",");
							}
							System.out.println();
						}
					}
				});
				this.setDefaultCloseOperation(EXIT_ON_CLOSE);
			}
}