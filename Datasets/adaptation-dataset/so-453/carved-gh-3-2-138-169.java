public class foo{
	// http://stackoverflow.com/questions/4946295/android-expand-collapse-animation
	public void expand() {
		final View v = (View) this;

		// only expand if it is not expanded
		if (v.getVisibility() != View.VISIBLE) {
			v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			final int targtetHeight = v.getMeasuredHeight();

			v.getLayoutParams().height = 0;
			v.setVisibility(View.VISIBLE);
			Animation a = new Animation() {
				@Override
				protected void applyTransformation(float interpolatedTime,
						Transformation t) {
					v.getLayoutParams().height = interpolatedTime == 1 ? LayoutParams.WRAP_CONTENT
							: (int) (targtetHeight * interpolatedTime);
					v.requestLayout();
				}

				@Override
				public boolean willChangeBounds() {
					return true;
				}
			};

			// 1dp/ms
			a.setDuration((int) (targtetHeight / v.getContext().getResources()
					.getDisplayMetrics().density) * 2);
			v.startAnimation(a);
		}
	}
}