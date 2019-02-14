public class foo{
    Bitmap getIconForPackage(Context mContext, String componentName, Bitmap defaultBitmap) {
        if(!mLoaded) load(mContext);

        String drawable = mPackagesDrawables.get(componentName);
        if(drawable != null) {
            Bitmap BMP = loadBitmap(mContext, drawable);
            if(BMP == null) {
                return generateBitmap(componentName, defaultBitmap);
            } else {
                return BMP;
            }
        } else {
            // Try to get a resource with the component filename
            if(componentName != null) {
                int start = componentName.indexOf("{") + 1;
                int end = componentName.indexOf("}", start);
                if(end > start) {
                    drawable = componentName.substring(start, end).toLowerCase(Locale.getDefault()).replace(".", "_").replace("/", "_");
                    if(getResources(mContext).getIdentifier(drawable, "drawable", packageName) > 0)
                        return loadBitmap(mContext, drawable);
                }
            }
        }

        return generateBitmap(componentName, defaultBitmap);
    }
}