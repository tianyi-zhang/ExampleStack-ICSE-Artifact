public class foo{
	public static void addListenerDeeply(final Node node, final EventHandler<MouseEvent> listener) {
		node.addEventHandler(MouseEvent.MOUSE_MOVED, listener);
		node.addEventHandler(MouseEvent.MOUSE_PRESSED, listener);
		node.addEventHandler(MouseEvent.MOUSE_DRAGGED, listener);
		if (node instanceof Parent) {
			final Parent parent = (Parent) node;
			final ObservableList<Node> children = parent.getChildrenUnmodifiable();
			for (final Node child : children) {
				ResizeAndMoveHelper.addListenerDeeply(child, listener);
			}
		}
	}
}