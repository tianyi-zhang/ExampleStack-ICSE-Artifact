public class foo{
	public static void addResizeListener(final Stage stage, final BorderPane menu) {
		final ResizeListener resizeListener = new ResizeListener(stage, menu);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_MOVED, resizeListener);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, resizeListener);
		stage.getScene().addEventHandler(MouseEvent.MOUSE_DRAGGED, resizeListener);
		final ObservableList<Node> children = stage.getScene().getRoot().getChildrenUnmodifiable();
		for (final Node child : children) {
			ResizeAndMoveHelper.addListenerDeeply(child, resizeListener);
		}
	}
}