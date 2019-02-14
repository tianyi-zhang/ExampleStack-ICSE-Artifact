public class foo {
@Override
public void setImageBitmap(Bitmap bm) {
    final Drawable drawable = new BitmapDrawable(getContext().getResources(), bm);
    setImageDrawable(drawable);

    AlphaSatColorMatrixEvaluator evaluator = new AlphaSatColorMatrixEvaluator();
    final AnimateColorMatrixColorFilter filter = new AnimateColorMatrixColorFilter(evaluator.getColorMatrix());
    drawable.setColorFilter(filter.getColorFilter());

    ObjectAnimator animator = ObjectAnimator.ofObject(filter, "colorMatrix", evaluator, evaluator.getColorMatrix());

    animator.addUpdateListener( new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            drawable.setColorFilter(filter.getColorFilter());
        }
    });
    animator.setDuration(1500);
    animator.start();
}
}