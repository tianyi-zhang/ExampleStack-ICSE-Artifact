public class foo {
@Override
protected void onLayout(boolean changed, int left, int top, int right, int bottom)
{
    if (changed)
    {                            
        final View cameraView = getChildAt(0);          

        final int width = right - left;
        final int height = bottom - top;

        int previewWidth = width;
        int previewHeight = height;
        if (mPreviewSize != null)
        {
            Display display = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

            switch (display.getRotation())
            {
                case Surface.ROTATION_0:
                    previewWidth = mPreviewSize.height;
                    previewHeight = mPreviewSize.width;
                    mCamera.setDisplayOrientation(90);
                    break;
                case Surface.ROTATION_90:
                    previewWidth = mPreviewSize.width;
                    previewHeight = mPreviewSize.height;
                    break;
                case Surface.ROTATION_180:
                    previewWidth = mPreviewSize.height;
                    previewHeight = mPreviewSize.width;
                    break;
                case Surface.ROTATION_270:
                    previewWidth = mPreviewSize.width;
                    previewHeight = mPreviewSize.height;
                    mCamera.setDisplayOrientation(180);
                    break;
            }                                    
        }

        final int scaledChildHeight = previewHeight * width / previewWidth;

        cameraView.layout(0, height - scaledChildHeight, width, height);

    }
}
}