public class foo {
        public Bitmap getIconForPackage(String appPackageName, Bitmap defaultBitmap)
        {
            if (!mLoaded)
                load();

            PackageManager pm = mContext.getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage(appPackageName);
            String componentName = null;
            if (launchIntent != null)
                componentName = pm.getLaunchIntentForPackage(appPackageName).getComponent().toString();
            String drawable = mPackagesDrawables.get(componentName);
            if (drawable != null)
            {
                return loadBitmap(drawable);
            }
            else
            {
                // try to get a resource with the component filename
                if (componentName != null)
                {
                    int start = componentName.indexOf("{")+1;
                    int end = componentName.indexOf("}",  start);
                    if (end > start)
                    {
                        drawable = componentName.substring(start,end).toLowerCase(Locale.getDefault()).replace(".","_").replace("/", "_");
                        if (iconPackres.getIdentifier(drawable, "drawable", packageName) > 0)
                            return loadBitmap(drawable);
                    }
                }
            }
            return generateBitmap(appPackageName, defaultBitmap);
        }
}