public class foo{
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mButtonDrawable != null) {
			mButtonDrawable.setState(getDrawableState());
			final int verticalGravity = getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
			final int height = mButtonDrawable.getIntrinsicHeight();

			int y = 0;
			switch (verticalGravity) {
			case Gravity.BOTTOM:
				y = getHeight() - height;
				break;

			case Gravity.CENTER_VERTICAL:
				y = (getHeight() - height) / 2;
				break;
			}

			int buttonWidth = mButtonDrawable.getIntrinsicWidth();
			int buttonLeft = (getWidth() - buttonWidth) / 2;
			mButtonDrawable.setBounds(buttonLeft, y, buttonLeft + buttonWidth, y + height);
			mButtonDrawable.draw(canvas);
		}
	}
}