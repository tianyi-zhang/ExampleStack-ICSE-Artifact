public class foo {
@SuppressLint("NewApi")
public static void setEdgeEffectColor(final EdgeEffect edgeEffect, final int color) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            edgeEffect.setColor(color);
            return;
        }
        final Field edgeField = EdgeEffect.class.getDeclaredField("mEdge");
        final Field glowField = EdgeEffect.class.getDeclaredField("mGlow");
        edgeField.setAccessible(true);
        glowField.setAccessible(true);
        final Drawable mEdge = (Drawable) edgeField.get(edgeEffect);
        final Drawable mGlow = (Drawable) glowField.get(edgeEffect);
        mEdge.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        mGlow.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        mEdge.setCallback(null); // free up any references
        mGlow.setCallback(null); // free up any references
    } catch (final Exception ignored) {
    }
}
}