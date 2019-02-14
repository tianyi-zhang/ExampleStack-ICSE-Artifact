public class foo {
     @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if (buttonDrawable != null) {
                buttonDrawable.setState(getDrawableState());
                final int verticalGravity = getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
                final int height = buttonDrawable.getIntrinsicHeight();

                int y = 0;

                switch (verticalGravity) {
                    case Gravity.BOTTOM:
                        y = getHeight() - height;
                        break;
                    case Gravity.CENTER_VERTICAL:
                        y = (getHeight() - height) / 2;
                        break;
                }

            int buttonWidth = buttonDrawable.getIntrinsicWidth();
            int buttonLeft = (getWidth() - buttonWidth) / 2;
            buttonDrawable.setBounds(buttonLeft, y, buttonLeft+buttonWidth, y + height);
                buttonDrawable.draw(canvas);
            }
        }
}