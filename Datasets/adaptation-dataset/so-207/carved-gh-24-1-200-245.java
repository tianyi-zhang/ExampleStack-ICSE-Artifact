public class foo{
  /**
   * Get the optimal preview size.
   * based on http://stackoverflow.com/questions/19577299/android-camera-preview-stretched 
   * 
   * @param int w Width of the wanted preview.
   * @param int h Height of the wanted preview.
   * 
   * @return Camera.Size Optimal resolution.
   */
  public static Camera.Size getOptimalPreviewSize(int w, int h) {
    if (ManagerCamera.mCamera == null) {
      return null;
    }
    List<Camera.Size> sizes = ManagerCamera.mCamera.getParameters().getSupportedPreviewSizes();
    
    if (sizes == null) {
      return null;
    }
    
    final double ASPECT_TOLERANCE = 0.1;
    double targetRatio = (double)h / w;
    Camera.Size optimalSize = null;
    double minDiff = Double.MAX_VALUE;

    int targetHeight = h;

    for (Camera.Size size : sizes) {
        double ratio = (double) size.width / size.height;
        if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
        if (Math.abs(size.height - targetHeight) < minDiff) {
            optimalSize = size;
            minDiff = Math.abs(size.height - targetHeight);
        }
    }

    if (optimalSize == null) {
        for (Camera.Size size : sizes) {
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
    }

    return optimalSize;
  }
}