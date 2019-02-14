public class foo {
public static void setCursorDrawableColor(EditText editText, int color) {
    try {
        Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
        fCursorDrawableRes.setAccessible(true);
        int mCursorDrawableRes = fCursorDrawableRes.getInt(editText);
        Field fEditor = TextView.class.getDeclaredField("mEditor");
        fEditor.setAccessible(true);
        Object editor = fEditor.get(editText);
        Class<?> clazz = editor.getClass();
        Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
        fCursorDrawable.setAccessible(true);
        Drawable[] drawables = new Drawable[2];
        drawables[0] = editText.getContext().getResources().getDrawable(mCursorDrawableRes);
        drawables[1] = editText.getContext().getResources().getDrawable(mCursorDrawableRes);
        drawables[0].setColorFilter(color, PorterDuff.Mode.SRC_IN);
        drawables[1].setColorFilter(color, PorterDuff.Mode.SRC_IN);
        fCursorDrawable.set(editor, drawables);
    } catch (Throwable ignored) {
    }
}
}