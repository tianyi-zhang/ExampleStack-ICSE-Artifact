public class foo{
	/**
	 * This method adds select-on-focus functionality to a {@link Text} component.
	 * 
	 * Specific behavior: - when the Text is already focused -> normal behavior - when the Text is not focused: -> focus
	 * by keyboard -> select all text -> focus by mouse click -> select all text unless user manually selects text
	 * 
	 * @param text
	 */
	public static void addSelectOnFocusToText(Text text) {
		// THIS METHOD WAS TAKEN FROM THE FOLLOWING TOPIC ON STACKOVERFLOW:
		// http://stackoverflow.com/questions/10038570/implementing-select-on-focus-behavior-for-an-eclipse-text-control
		// REFER ALSO TO THE FOLLOWING ECLIPSE BUG 46059
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=46059
		Listener listener = new Listener() {

			private boolean hasFocus = false;
			private boolean hadFocusOnMousedown = false;

			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.FocusIn: {
					Text t = (Text) e.widget;

					// Covers the case where the user focuses by keyboard.
					t.selectAll();

					// The case where the user focuses by mouse click is special because Eclipse,
					// for some reason, fires SWT.FocusIn before SWT.MouseDown, and on mouse down
					// it cancels the selection. So we set a variable to keep track of whether the
					// control is focused (can't rely on isFocusControl() because sometimes it's wrong),
					// and we make it asynchronous so it will get set AFTER SWT.MouseDown is fired.
					t.getDisplay().asyncExec(new Runnable() {
						public void run() {
							hasFocus = true;
						}
					});

					break;
				}
				case SWT.FocusOut: {
					hasFocus = false;
					((Text) e.widget).clearSelection();

					break;
				}
				case SWT.MouseDown: {
					// Set the variable which is used in SWT.MouseUp.
					hadFocusOnMousedown = hasFocus;

					break;
				}
				case SWT.MouseUp: {
					Text t = (Text) e.widget;
					if (t.getSelectionCount() == 0 && !hadFocusOnMousedown) {
						((Text) e.widget).selectAll();
					}

					break;
				}
				}
			}

		};

		text.addListener(SWT.FocusIn, listener);
		text.addListener(SWT.FocusOut, listener);
		text.addListener(SWT.MouseDown, listener);
		text.addListener(SWT.MouseUp, listener);
	}
}