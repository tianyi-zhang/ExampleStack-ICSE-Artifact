public class foo{
		@Override
		public void handle(final MouseEvent mouseEvent) {
			final EventType<? extends MouseEvent> mouseEventType = mouseEvent.getEventType();
			final Scene scene = this.stage.getScene();

			final double mouseEventX = mouseEvent.getSceneX(), mouseEventY = mouseEvent.getSceneY(), sceneWidth = scene.getWidth(), sceneHeight = scene.getHeight();

			if (MouseEvent.MOUSE_MOVED.equals(mouseEventType) == true) {
				if (mouseEventX < this.border && mouseEventY < this.border) {
					this.cursorEvent = Cursor.NW_RESIZE;
				} else if (mouseEventX < this.border && mouseEventY > sceneHeight - this.border) {
					this.cursorEvent = Cursor.SW_RESIZE;
				} else if (mouseEventX > sceneWidth - this.border && mouseEventY < this.border) {
					this.cursorEvent = Cursor.NE_RESIZE;
				} else if (mouseEventX > sceneWidth - this.border && mouseEventY > sceneHeight - this.border) {
					this.cursorEvent = Cursor.SE_RESIZE;
				} else if (mouseEventX < this.border) {
					this.cursorEvent = Cursor.W_RESIZE;
				} else if (mouseEventX > sceneWidth - this.border) {
					this.cursorEvent = Cursor.E_RESIZE;
				} else if (mouseEventY < this.border) {
					this.cursorEvent = Cursor.N_RESIZE;
				} else if (mouseEventY > sceneHeight - this.border) {
					this.cursorEvent = Cursor.S_RESIZE;
				} else {
					this.cursorEvent = Cursor.DEFAULT;

				}
				scene.setCursor(this.cursorEvent);
			} else if (MouseEvent.MOUSE_PRESSED.equals(mouseEventType) == true) {
				this.startX = this.stage.getWidth() - mouseEventX;
				this.startY = this.stage.getHeight() - mouseEventY;
			} else if (MouseEvent.MOUSE_DRAGGED.equals(mouseEventType) == true) {
				this.resizing = false;
				if (Cursor.DEFAULT.equals(this.cursorEvent) == false) {
					if (Cursor.W_RESIZE.equals(this.cursorEvent) == false && Cursor.E_RESIZE.equals(this.cursorEvent) == false) {
						final double minHeight = this.stage.getMinHeight() > (this.border * 2) ? this.stage.getMinHeight() : (this.border * 2);
						if (Cursor.NW_RESIZE.equals(this.cursorEvent) == true || Cursor.N_RESIZE.equals(this.cursorEvent) == true || Cursor.NE_RESIZE.equals(this.cursorEvent) == true) {
							if (this.stage.getHeight() > minHeight || mouseEventY < 0) {
								this.resizing = true;
								this.stage.setHeight(this.stage.getY() - mouseEvent.getScreenY() + this.stage.getHeight());
								this.stage.setY(mouseEvent.getScreenY());
							}
						} else {
							if (this.stage.getHeight() > minHeight || mouseEventY + this.startY - this.stage.getHeight() > 0) {
								this.resizing = true;
								this.stage.setHeight(mouseEventY + this.startY);
							}
						}
					}

					if (Cursor.N_RESIZE.equals(this.cursorEvent) == false && Cursor.S_RESIZE.equals(this.cursorEvent) == false) {
						final double minWidth = this.stage.getMinWidth() > (this.border * 2) ? this.stage.getMinWidth() : (this.border * 2);
						if (Cursor.NW_RESIZE.equals(this.cursorEvent) == true || Cursor.W_RESIZE.equals(this.cursorEvent) == true || Cursor.SW_RESIZE.equals(this.cursorEvent) == true) {
							if (this.stage.getWidth() > minWidth || mouseEventX < 0) {
								this.resizing = true;
								this.stage.setWidth(this.stage.getX() - mouseEvent.getScreenX() + this.stage.getWidth());
								this.stage.setX(mouseEvent.getScreenX());
							}
						} else {
							if (this.stage.getWidth() > minWidth || mouseEventX + this.startX - this.stage.getWidth() > 0) {
								this.resizing = true;
								this.stage.setWidth(mouseEventX + this.startX);
							}
						}
					}
				}

			}
		}
}