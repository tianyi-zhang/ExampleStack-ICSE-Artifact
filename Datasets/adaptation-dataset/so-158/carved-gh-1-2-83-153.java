public class foo{
	/**
	 * ViewPager does not respect "wrap_content". The code below tries to
	 * measure the height of the child and set the height of viewpager based on
	 * child height
	 * 
	 * It was customized from
	 * http://stackoverflow.com/questions/9313554/measuring-a-viewpager
	 * 
	 * Thanks Delyan for his brilliant code
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// Calculate row height
		int rows = datesInMonth.size() / 7;

		boolean wrapHeight = MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST;

		int height = getMeasuredHeight();
		if (wrapHeight && rowHeight == 0) {
			/*
			 * The first super.onMeasure call made the pager take up all the
			 * available height. Since we really wanted to wrap it, we need to
			 * remeasure it. Luckily, after that call the first child is now
			 * available. So, we take the height from it.
			 */

			int width = getMeasuredWidth();

			// Use the previously measured width but simplify the calculations
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,
					MeasureSpec.EXACTLY);

			/*
			 * If the pager actually has any children, take the first child's
			 * height and call that our own
			 */
			if (getChildCount() > 0) {
				View firstChild = getChildAt(0);

				/*
				 * The child was previously measured with exactly the full
				 * height. Allow it to wrap this time around.
				 */
				firstChild.measure(widthMeasureSpec, MeasureSpec
						.makeMeasureSpec(height, MeasureSpec.AT_MOST));

				height = firstChild.getMeasuredHeight();
				rowHeight = height / rows;
			}
		}

		// Calculate height of the calendar
		int calHeight = 0;

		// If fit 6 weeks, we need 6 rows
		if (sixWeeksInCalendar) {
			calHeight = rowHeight * 6;
		} else { // Otherwise we return correct number of rows
			calHeight = rowHeight * rows;
		}

		// Prevent small vertical scroll
		calHeight += 3;

		heightMeasureSpec = MeasureSpec.makeMeasureSpec(calHeight,
				MeasureSpec.EXACTLY);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}