public class foo{
    // http://stackoverflow.com/questions/5540981/picture-distorted-with-camera-and-getoptimalpreviewsize
    private Camera.Size getBestPreviewSize(int width, int height)
    {
            Camera.Size result=null;    
            Camera.Parameters p = mCamera.getParameters();
            for (Camera.Size size : p.getSupportedPreviewSizes()) {
                if (size.width<=width && size.height<=height) {
                    if (result==null) {
                        result=size;
                    } else {
                        int resultArea=result.width*result.height;
                        int newArea=size.width*size.height;

                        if (newArea>resultArea) {
                            result=size;
                        }
                    }
                }
            }
        return result;

    }
}