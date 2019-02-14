public class foo {
public Drawable fetchDrawable(String urlString) {
    SoftReference<Drawable> drawableRef = drawableMap.get(urlString);
    if (drawableRef != null) {
        Drawable drawable = drawableRef.get();
        if (drawable != null)
            return drawable;
        // Reference has expired so remove the key from drawableMap
        drawableMap.remove(urlString);
    }

    if (Constants.LOGGING) Log.d(this.getClass().getSimpleName(), "image url:" + urlString);
    try {
        InputStream is = fetch(urlString);
        Drawable drawable = Drawable.createFromStream(is, "src");
        drawableRef = new SoftReference<Drawable>(drawable);
        drawableMap.put(urlString, drawableRef);
        if (Constants.LOGGING) Log.d(this.getClass().getSimpleName(), "got a thumbnail drawable: " + drawable.getBounds() + ", "
                + drawable.getIntrinsicHeight() + "," + drawable.getIntrinsicWidth() + ", "
                + drawable.getMinimumHeight() + "," + drawable.getMinimumWidth());
        return drawableRef.get();
    } catch (MalformedURLException e) {
        if (Constants.LOGGING) Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
        return null;
    } catch (IOException e) {
        if (Constants.LOGGING) Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
        return null;
    }
}
}