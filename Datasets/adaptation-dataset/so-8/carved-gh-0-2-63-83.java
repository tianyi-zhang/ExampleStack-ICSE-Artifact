public class foo{
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// http://stackoverflow.com/questions/13992535/android-imageview-scale-smaller-image-to-width-with-flexible-height-without-crop
		// HACK: Auto-fit height/width and leave the other one flexible (keep ratio)
		Drawable d = getDrawable();
		
		if (d != null) {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = MeasureSpec.getSize(heightMeasureSpec);
			
			if (width >= height) {
				height = (int) Math.ceil(width * (float) d.getIntrinsicHeight() / d.getIntrinsicWidth());
			} else {
				width = (int) Math.ceil(height * (float) d.getIntrinsicWidth() / d.getIntrinsicHeight());
			}
			
			setMeasuredDimension(width, height);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
}