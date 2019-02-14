public class foo{
    private void load(Context mContext) {
        if(!mIsLoading) {
            mIsLoading = true;

            SharedPreferences pref = U.getSharedPreferences(mContext);
            boolean loadMasks = pref.getBoolean("icon_pack_use_mask", false);

            // Load appfilter.xml from the icon pack package
            try {
                XmlPullParser xpp = null;

                int appfilterid = getResources(mContext).getIdentifier("appfilter", "xml", packageName);
                if(appfilterid > 0) {
                    xpp = getResources(mContext).getXml(appfilterid);
                } else {
                    // No resource found, try to open it from assets folder
                    try {
                        InputStream appfilterstream = getResources(mContext).getAssets().open("appfilter.xml");

                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        factory.setNamespaceAware(true);
                        xpp = factory.newPullParser();
                        xpp.setInput(appfilterstream, "utf-8");
                    } catch (IOException e) { /* Gracefully fail */ }
                }

                if(xpp != null) {
                    int eventType = xpp.getEventType();
                    while(eventType != XmlPullParser.END_DOCUMENT) {
                        if(eventType == XmlPullParser.START_TAG) {
                            if(loadMasks) {
                                if(xpp.getName().equals("iconback")) {
                                    for(int i = 0; i < xpp.getAttributeCount(); i++) {
                                        if(xpp.getAttributeName(i).startsWith("img")) {
                                            String drawableName = xpp.getAttributeValue(i);
                                            Bitmap iconback = loadBitmap(mContext, drawableName);
                                            if(iconback != null)
                                                mBackImages.add(iconback);
                                        }
                                    }
                                } else if(xpp.getName().equals("iconmask")) {
                                    if(xpp.getAttributeCount() > 0 && xpp.getAttributeName(0).equals("img1")) {
                                        String drawableName = xpp.getAttributeValue(0);
                                        mMaskImage = loadBitmap(mContext, drawableName);
                                    }
                                } else if(xpp.getName().equals("iconupon")) {
                                    if(xpp.getAttributeCount() > 0 && xpp.getAttributeName(0).equals("img1")) {
                                        String drawableName = xpp.getAttributeValue(0);
                                        mFrontImage = loadBitmap(mContext, drawableName);
                                    }
                                } else if(xpp.getName().equals("scale")) {
                                    if(xpp.getAttributeCount() > 0 && xpp.getAttributeName(0).equals("factor")) {
                                        mFactor = Float.valueOf(xpp.getAttributeValue(0));
                                    }
                                }
                            }

                            if(xpp.getName().equals("item")) {
                                String componentName = null;
                                String drawableName = null;

                                for(int i = 0; i < xpp.getAttributeCount(); i++) {
                                    if(xpp.getAttributeName(i).equals("component")) {
                                        componentName = xpp.getAttributeValue(i);
                                    } else if(xpp.getAttributeName(i).equals("drawable")) {
                                        drawableName = xpp.getAttributeValue(i);
                                    }
                                }
                                if(!mPackagesDrawables.containsKey(componentName)) {
                                    mPackagesDrawables.put(componentName, drawableName);
                                    totalIcons = totalIcons + 1;
                                }
                            }
                        }
                        eventType = xpp.next();
                    }
                }

                mLoaded = true;
            } catch (XmlPullParserException | IOException e) { /* Gracefully fail */ }

            mIsLoading = false;
        }
    }
}