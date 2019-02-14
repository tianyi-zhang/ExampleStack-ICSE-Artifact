package org.geneanet.customcamera;

import java.util.List;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.Surface;

/**
 * Manage camera resource.
 */
public class ManagerCamera {
  protected static Camera mCamera = null;

  // Constant to define different orientations for the devices.
  public static final int PORTRAIT = 0;
  public static final int LANDSCAPE = 1;
  public static final int PORTRAIT_INVERSED = 2;
  public static final int LANDSCAPE_INVERSED = 3;
  private static Integer currentCameraPosition = null;

  /**
   * A safe way to get an instance of the Camera object.
   * 
   * @return Camera | null
   */
  public static Camera getCameraInstance(int position) {
    // If camera is already instanced and available, return this resource.
    if (ManagerCamera.mCamera != null && position == currentCameraPosition) {
      return ManagerCamera.mCamera;
    } else if (ManagerCamera.mCamera != null) {
      clearCameraAccess();
    }

    // Start back camera.
    Camera cam = null;
    try {
      cam = Camera.open(position);
      currentCameraPosition = position;
    } catch (RuntimeException e) {
      Log.e("customCamera", "Can't open the camera back.");
    }
    ManagerCamera.mCamera = cam;

    return cam; // returns null if camera is unavailable
  }

  /**
   * To release the camera.
   */
  public static void clearCameraAccess() {
    if (ManagerCamera.mCamera != null) {
      ManagerCamera.mCamera.stopPreview();
      ManagerCamera.mCamera.release();
      ManagerCamera.mCamera = null;
    }
  }
  
  /**
   * Return the value of the position of the front camera.
   * 
   * @return int 
   */
  public static int determinePositionFrontCamera() {
    return determineCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
  }
  
  /**
   * Return the value of the position of the back camera.
   * 
   * @return int 
   */
  public static int determinePositionBackCamera() {
    return determineCamera(Camera.CameraInfo.CAMERA_FACING_BACK); 
  }
  
  /**
   * Determine the opposite camera of which currently in use.
   * 
   * @return int.
   */
  public static int determineOppositeCamera() {
    if (getCurrentFacingCamera() == Camera.CameraInfo.CAMERA_FACING_BACK) {
      return determinePositionFrontCamera();
    } else {
      return determinePositionBackCamera();
    }
  }
  
  /**
   * Determine the cameraId of the camera currently in use.
   * 
   * @param position Back or front camera.
   * 
   * @return the cameraId of the current camera if it exists.
   */
  protected static Integer determineCamera(int position) {
    CameraInfo info = new Camera.CameraInfo();
    if (Camera.getNumberOfCameras() == 0) {
      return null;
    }
    if (Camera.getNumberOfCameras() == 1) {
      return 0;
    }
    for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
      Camera.getCameraInfo(i, info);
      if (info.facing == position) {
        return i;
      }
    }
    
    return 0;
  }
  
  /**
   * Get the current camera.
   * 
   * @return the value of currentCameraPosition.
   */
  private static int getCurrentFacingCamera() {
    CameraInfo info = new Camera.CameraInfo();
    Camera.getCameraInfo(currentCameraPosition, info);
    
    return info.facing;
  }
  
  /**
   * Determine if the current camera is front.
   * 
   * @return True if the current camera is front. Else return false.
   */
  public static boolean currentCameraIsFacingBack() {
    if (getCurrentFacingCamera() == Camera.CameraInfo.CAMERA_FACING_BACK) {
      return true;
    }
    return false;
  }
  
  /**
   * Determine if the camera is back.
   * 
   * @return True if the current camera is back. Else return false.
   */
  public static boolean currentCameraIsFacingFront() {
    if (getCurrentFacingCamera() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
      return true;
    }
    return false;
  }
  
  /**
   * To stabilize the orientation of the camera preview.
   * @param activity
   * @param cameraId
   * @param camera
   */
  public static void setCameraDisplayOrientation(Activity activity) {
    CameraInfo info = new Camera.CameraInfo();
    Camera.getCameraInfo(ManagerCamera.currentCameraPosition, info);
    int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
    int degrees = 0;
    switch (rotation) {
      case Surface.ROTATION_0: degrees = 0; break;
      case Surface.ROTATION_90: degrees = 90; break;
      case Surface.ROTATION_180: degrees = 180; break;
      case Surface.ROTATION_270: degrees = 270; break;
      default : break;
    }

    int result;
    if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
      result = (info.orientation + degrees) % 360;
      result = (360 - result) % 360;  // compensate the mirror
    } else {  // back-facing
      result = (info.orientation - degrees + 360) % 360;
    }
    ManagerCamera.mCamera.setDisplayOrientation(result);
  }
  
  /**
   * Get the value of currentCameraPosition.
   * 
   * @return Integer.
   */
  public static int getCurrentCameraPosition() {
    return ManagerCamera.currentCameraPosition;
  }
  
  /**
   * Get the value of mCamera.
   * 
   * @return Camera.
   */
  public static Camera getCurrentCameraResource() {
    return ManagerCamera.mCamera;
  }
  
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
  
  /**
   * Get the optimal picture size. 
   * 
   * @param int w Width of the wanted picture.
   * @param int h Height of the wanted picture.
   * 
   * @return Camera.Size Optimal resolution.
   */
  public static Camera.Size getOptimalPictureSize(int w, int h) {
    List<Camera.Size> pictureSizes = ManagerCamera.mCamera.getParameters().getSupportedPictureSizes();
    
    if (pictureSizes == null) {
      return null;
    }

    Camera.Size optimalSizePicture = null;
    for (Camera.Size size : pictureSizes) {
        if (
            ((float)size.width / (float)size.height) == ((float)w / (float)h) &&
            (
                optimalSizePicture == null ||
                optimalSizePicture.width < size.width
            )
        ) {
          optimalSizePicture = size;
        }
    }
    
    return optimalSizePicture;
  }
}
