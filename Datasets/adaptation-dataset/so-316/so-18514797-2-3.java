public class foo {
private void unbindDrawables(View view) {
  if (view.getBackground() != null)
    view.getBackground().setCallback(null);

  if (view instanceof ImageView) {
    ImageView imageView = (ImageView) view;
    imageView.setImageBitmap(null);
  } else if (view instanceof ViewGroup) {
    ViewGroup viewGroup = (ViewGroup) view;
    for (int i = 0; i < viewGroup.getChildCount(); i++)
    unbindDrawables(viewGroup.getChildAt(i));

    if (!(view instanceof AdapterView))
      viewGroup.removeAllViews();
  }
}
}