public class foo{
	public void collapse() {
		final View v = (View) this;
		// only collapse if it is already visible
		if (v.getVisibility() != View.GONE) {
			final int initialHeight = v.getMeasuredHeight();

			Animation a = new Animation() {
				@Override
				protected void applyTransformation(float interpolatedTime,
						Transformation t) {
					if (interpolatedTime == 1) {
						v.setVisibility(View.GONE);
					} else {
						v.getLayoutParams().height = initialHeight
								- (int) (initialHeight * interpolatedTime);
						v.requestLayout();
					}
				}

				@Override
				public boolean willChangeBounds() {
					return true;
				}
			};

			// 1dp/ms
			a.setDuration((int) (initialHeight / v.getContext().getResources()
					.getDisplayMetrics().density) * 2);
			v.startAnimation(a);
		}
	}
}