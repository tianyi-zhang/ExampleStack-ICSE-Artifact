public class foo{
    Drawable getDrawableIconForPackage(Context mContext, String componentName) {
        if(!mLoaded) load(mContext);

        String drawable = mPackagesDrawables.get(componentName);
        if(drawable != null) {
            return loadDrawable(mContext, drawable);
        } else {
            // Try to get a resource with the component filename
            if(componentName != null) {
                int start = componentName.indexOf("{") + 1;
                int end = componentName.indexOf("}", start);
                if(end > start) {
                    drawable = componentName.substring(start, end).toLowerCase(Locale.getDefault()).replace(".", "_").replace("/", "_");
                    if(getResources(mContext).getIdentifier(drawable, "drawable", packageName) > 0)
                        return loadDrawable(mContext, drawable);
                }
            }
        }

        return null;
    }
}