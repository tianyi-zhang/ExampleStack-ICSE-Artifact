public class foo{
    public static void setEdgeEffectColor(final EdgeEffect edgeEffect, @ColorRes final int color) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                edgeEffect.setColor(color);
                return;
            }
            final Field edgeField = EdgeEffect.class.getDeclaredField("mEdge");
            final Field glowField = EdgeEffect.class.getDeclaredField("mGlow");
            edgeField.setAccessible(true);
            glowField.setAccessible(true);
            final Drawable edge = (Drawable) edgeField.get(edgeEffect);
            final Drawable glow = (Drawable) glowField.get(edgeEffect);
            edge.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            glow.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            edge.setCallback(null); // free up any references
            glow.setCallback(null); // free up any references
        } catch (final Exception ignored) {
            ignored.printStackTrace();
        }
    }
}