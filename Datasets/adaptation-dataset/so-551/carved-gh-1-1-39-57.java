public class foo{
    /**
     * Source: http://stackoverflow.com/questions/18374183/how-to-show-icons-in-overflow-menu-in-actionbar
     */
    public static void showMenuIcons(final Context context, final Menu menu) {
        // It seams that the setOptionalIconsVisible code is very old and does not support RTL layouts.
        // Thus we hide the icons for the few RTL users instead of showing them in an ugly way
        if (!ViewHelper.isRTL(context) && menu instanceof MenuBuilder) {
            try {
                final Method m = menu.getClass().getDeclaredMethod(
                                     "setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, true);
            } catch (final NoSuchMethodException e) {
                Log.e(TAG, "onMenuOpened", e);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}