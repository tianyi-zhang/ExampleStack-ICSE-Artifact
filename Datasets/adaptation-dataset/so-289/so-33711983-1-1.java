public class foo {
        public void load()
        {
            // load appfilter.xml from the icon pack package
            PackageManager pm = mContext.getPackageManager();
            try
            {
                XmlPullParser xpp = null;

                iconPackres = pm.getResourcesForApplication(packageName);
                int appfilterid = iconPackres.getIdentifier("appfilter", "xml", packageName);
                if (appfilterid > 0)
                {
                    xpp = iconPackres.getXml(appfilterid);
                }
                else
                {
                    // no resource found, try to open it from assests folder
                    try
                    {
                        InputStream appfilterstream = iconPackres.getAssets().open("appfilter.xml");

                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        factory.setNamespaceAware(true);
                        xpp = factory.newPullParser();
                        xpp.setInput(appfilterstream, "utf-8");
                    }
                    catch (IOException e1)
                    {
                        //Ln.d("No appfilter.xml file");
                    }
                }

                if (xpp != null)
                {
                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT)
                    {
                        if(eventType == XmlPullParser.START_TAG)
                        {
                            if (xpp.getName().equals("iconback"))
                            {
                                for(int i=0; i<xpp.getAttributeCount(); i++)
                                {
                                    if (xpp.getAttributeName(i).startsWith("img"))
                                    {
                                        String drawableName = xpp.getAttributeValue(i);
                                        Bitmap iconback = loadBitmap(drawableName);
                                        if (iconback != null)
                                            mBackImages.add(iconback);
                                    }
                                }
                            }
                            else if (xpp.getName().equals("iconmask"))
                            {
                                if (xpp.getAttributeCount() > 0 && xpp.getAttributeName(0).equals("img1"))
                                {
                                    String drawableName = xpp.getAttributeValue(0);
                                    mMaskImage = loadBitmap(drawableName);
                                }
                            }
                            else if (xpp.getName().equals("iconupon"))
                            {
                                if (xpp.getAttributeCount() > 0 && xpp.getAttributeName(0).equals("img1"))
                                {
                                    String drawableName = xpp.getAttributeValue(0);
                                    mFrontImage = loadBitmap(drawableName);
                                }
                            }
                            else if (xpp.getName().equals("scale"))
                            {
                                // mFactor
                                if (xpp.getAttributeCount() > 0 && xpp.getAttributeName(0).equals("factor"))
                                {
                                    mFactor = Float.valueOf(xpp.getAttributeValue(0));
                                }
                            }
                            else if (xpp.getName().equals("item"))
                            {
                                String componentName = null;
                                String drawableName = null;

                                for(int i=0; i<xpp.getAttributeCount(); i++)
                                {
                                    if (xpp.getAttributeName(i).equals("component"))
                                    {
                                        componentName = xpp.getAttributeValue(i);
                                    }
                                    else if (xpp.getAttributeName(i).equals("drawable"))
                                    {
                                        drawableName = xpp.getAttributeValue(i);
                                    }
                                }
                                if (!mPackagesDrawables.containsKey(componentName)) {
                                    mPackagesDrawables.put(componentName, drawableName);
                                    totalIcons = totalIcons + 1;
                                }
                            }
                        }
                        eventType = xpp.next();
                    }
                }
                mLoaded = true;
            }
            catch (PackageManager.NameNotFoundException e)
            {
                //Ln.d("Cannot load icon pack");
            }
            catch (XmlPullParserException e)
            {
                //Ln.d("Cannot parse icon pack appfilter.xml");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
}