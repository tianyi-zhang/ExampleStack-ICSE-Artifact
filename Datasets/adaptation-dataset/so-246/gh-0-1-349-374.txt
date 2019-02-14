/*
Created by Chris Risner
Copyright (c) Microsoft Corporation
All Rights Reserved
Apache 2.0 License
 
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
 
     http://www.apache.org/licenses/LICENSE-2.0
 
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 
See the Apache Version 2.0 License for specific language governing permissions and limitations under the License.
 */

package com.msted.lensrocket.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnScrollListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.msted.lensrocket.CameraPreview;
import com.msted.lensrocket.Constants;
import com.msted.lensrocket.PreferencesHandler;
import com.msted.lensrocket.Constants.CameraUIMode;
import com.msted.lensrocket.base.BaseActivity;
import com.msted.lensrocket.util.DeviceDetector;
import com.msted.lensrocket.util.NetworkUtilities;
import com.msted.lensrocket.util.LensRocketAlert;
import com.msted.lensrocket.util.LensRocketLogger;
import com.msted.lensrocket.R;

public class RecordActivity extends BaseActivity implements NumberPicker.OnValueChangeListener {
	
	private final String TAG = "RecordActivity";
	private Camera mCamera;
	private CameraPreview mCameraPreview;
	private VideoView mVideoView;
	private ImageView mImageView;
	private MediaRecorder mMediaRecorder;
	private ImageButton mBtnSwitchCamera;
	private ImageButton mBtnFlash;
	private ImageButton mBtnTakePicture;
	private ImageButton mBtnRockets;
	private ImageButton mBtnFriends;
	private ImageButton mBtnSend;
	private ImageButton mBtnDelete;
	private TextView mLblTime;
	private RelativeLayout mLayoutTime;
	private int mCameraNumber;
	private boolean mFlashOn;
	private boolean mTakingVideo;
	private boolean mReviewingPicture;
	private boolean mReviewingVideo;
	private String  mVideoFileName;
	private String  mFileFullPath;
	private File mMediaStorageDir;
	private FrameLayout mFrameLayout;
	private byte[] mPictureData;
	private boolean mIsScrolling;
	private int     mSecondsSelected;
	private boolean mIsReply;
	private boolean mIsSending = false;
	private String mReplyToUserId;
	private Bitmap mBitmapHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Make sure the user is authenticated in case of crash
		if (!mLensRocketService.isUserAuthenticated()) {	
			LensRocketLogger.i(TAG, "user is not authenticated");
			Intent intent = new Intent(mActivity, SplashScreenActivity.class);
			startActivity(intent);
			finish();
		}		
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_record);
		mBtnSwitchCamera = (ImageButton) findViewById(R.id.btnSwitchCameras);
		mBtnFlash = (ImageButton) findViewById(R.id.btnFlash);
		mBtnTakePicture = (ImageButton) findViewById(R.id.btnTakePicture);
		mBtnRockets = (ImageButton) findViewById(R.id.btnRockets);
		mBtnFriends = (ImageButton) findViewById(R.id.btnFriends);
		mVideoView = (VideoView) findViewById(R.id.videoView);
		mImageView = (ImageView) findViewById(R.id.pictureView2);
		mBtnSend = (ImageButton) findViewById(R.id.btnSend);
		mBtnDelete = (ImageButton) findViewById(R.id.btnDelete);
		mLayoutTime = (RelativeLayout) findViewById(R.id.layoutTime);		
		mLblTime = (TextView) findViewById(R.id.lblTime);		
		mBtnTakePicture.setOnClickListener(takePictureListener);
		mBtnTakePicture.setOnLongClickListener(takeVideoListener);
		mBtnTakePicture.setOnTouchListener(touchListener);
		if (NetworkUtilities.isNetworkOnline(mActivity)) {
			mLensRocketService.getFriends();			
			mLensRocketService.getRockets();
			mLensRocketService.getPreferences();
		} else {
			LensRocketAlert.showSimpleErrorDialog(mActivity, "You should connect to the internet and rerun LensRocket.");
		}
		mTakingVideo = false;
		mReviewingPicture = false;
		mReviewingVideo = false;
		mIsScrolling = false;
		mSecondsSelected = 3;		
		mLblTime.setText(mSecondsSelected + "");
		//TODO: Consider moving to background task
		mLensRocketService.registerForPush();
	}
	
	private OnClickListener takePictureListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			LensRocketLogger.i(TAG, "TakePic");			
			mCamera.takePicture(null, null, mPictureCallback);
			//Hide UI
			setUIMode(Constants.CameraUIMode.UI_MODE_TAKING_PICTURE);
		}	
	};
	
	private void setUIMode(Constants.CameraUIMode uiMode) {
		switch (uiMode) {
		case UI_MODE_REPLYING:
			mBtnFlash.setVisibility(View.VISIBLE);
			mBtnFriends.setVisibility(View.GONE);
			mBtnRockets.setVisibility(View.GONE);
			mBtnSwitchCamera.setVisibility(View.VISIBLE);
			mBtnTakePicture.setVisibility(View.VISIBLE);
			mBtnSend.setVisibility(View.GONE);
			mBtnDelete.setVisibility(View.GONE);
			mLayoutTime.setVisibility(View.GONE);			
			mCameraPreview.setVisibility(View.VISIBLE);
			mVideoView.setVisibility(View.GONE);
			mImageView.setVisibility(View.GONE);
			break;
		case UI_MODE_PRE_PICTURE:
			mBtnFlash.setVisibility(View.VISIBLE);
			mBtnFriends.setVisibility(View.VISIBLE);
			mBtnRockets.setVisibility(View.VISIBLE);
			mBtnSwitchCamera.setVisibility(View.VISIBLE);
			mBtnTakePicture.setVisibility(View.VISIBLE);
			mBtnSend.setVisibility(View.GONE);
			mBtnDelete.setVisibility(View.GONE);
			mLayoutTime.setVisibility(View.GONE);			
			mCameraPreview.setVisibility(View.VISIBLE);
			mVideoView.setVisibility(View.GONE);
			mImageView.setVisibility(View.GONE);
			break;
		case UI_MODE_REVIEW_PICTURE:
			mBtnFlash.setVisibility(View.GONE);
			mBtnFriends.setVisibility(View.GONE);
			mBtnRockets.setVisibility(View.GONE);
			mBtnSwitchCamera.setVisibility(View.GONE);
			mBtnTakePicture.setVisibility(View.GONE);
			mBtnSend.setVisibility(View.VISIBLE);
			mBtnDelete.setVisibility(View.VISIBLE);
			mLayoutTime.setVisibility(View.VISIBLE);
			mVideoView.setVisibility(View.GONE);						
			break;
		case UI_MODE_REVIEW_VIDEO:
			mBtnFlash.setVisibility(View.GONE);
			mBtnFriends.setVisibility(View.GONE);
			mBtnRockets.setVisibility(View.GONE);
			mBtnSwitchCamera.setVisibility(View.GONE);
			mBtnTakePicture.setVisibility(View.GONE);
			mBtnSend.setVisibility(View.VISIBLE);
			mBtnDelete.setVisibility(View.VISIBLE);
			mLayoutTime.setVisibility(View.VISIBLE);
			if (mVideoView == null)
				mVideoView = new VideoView(this);		
			mVideoView.setVideoPath(mFileFullPath);
			mCameraPreview.setVisibility(View.GONE);
			mVideoView.setVisibility(View.VISIBLE);		    
		    mVideoView.setOnPreparedListener(new OnPreparedListener() {				
				@Override
				public void onPrepared(MediaPlayer mp) {
					mp.setLooping(true);					
				}
			});
			mVideoView.start();
			break;
		case UI_MODE_TAKING_PICTURE:
			mBtnFlash.setVisibility(View.GONE);
			mBtnFriends.setVisibility(View.GONE);
			mBtnRockets.setVisibility(View.GONE);
			mBtnSwitchCamera.setVisibility(View.GONE);
			mBtnTakePicture.setVisibility(View.GONE);
			mBtnSend.setVisibility(View.GONE);
			break;
		case UI_MODE_TAKING_VIDEO:
			mBtnFlash.setVisibility(View.GONE);
			mBtnSwitchCamera.setVisibility(View.GONE);	
			mBtnSend.setVisibility(View.GONE);
			break;
		}
	}
	
	private OnLongClickListener takeVideoListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			LensRocketLogger.i(TAG, "Video start");			
			mTakingVideo = true;
			if (prepareVideoRecorder()) {				
				mMediaRecorder.start();
				setUIMode(Constants.CameraUIMode.UI_MODE_TAKING_VIDEO);
			} else {
				//TODO: show an error to the user
				releaseMediaRecorder();
			}
			return true;
		}		
	};
	
	private void releaseMediaRecorder() {
		if (mMediaRecorder != null) {
			mMediaRecorder.reset();
			mMediaRecorder.release();
			mMediaRecorder = null;			
		}
	}
	
	private boolean prepareVideoRecorder() {
		/**
		 * This code fixes an issue with the Galaxy S3 where you need to
		 * lock and release the camera in order for recording video to work
		 * Unfortunately, this breaks play back of videos on the Galaxy Nexus
		 * The suggested fix was found here:
		 * http://stackoverflow.com/questions/12696318/video-display-is-garbled-when-recording-on-galaxy-s3
		 **/
		if (DeviceDetector.isDeviceS3()) {
			mCamera.stopPreview();
			mCamera.lock();
			mCamera.release();
			mCamera = Camera.open(mCameraNumber);
			mCamera.setDisplayOrientation(90);
		}
		mMediaRecorder = new MediaRecorder();
		mCamera.unlock();
		mMediaRecorder.setCamera(mCamera);
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);		
		mMediaRecorder.setProfile(CamcorderProfile.get(mCameraNumber, CamcorderProfile.QUALITY_HIGH));		
		mMediaRecorder.setOutputFile(getOutputMediaFile(Constants.MEDIA_TYPE_VIDEO).toString());
		mMediaRecorder.setPreviewDisplay(mCameraPreview.getHolder().getSurface());
		mMediaRecorder.setOrientationHint(90);		
		DisplayMetrics metrics = new DisplayMetrics(); 
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		LensRocketLogger.d(TAG, "Width: " + metrics.widthPixels);
		LensRocketLogger.d(TAG, "Height: " + metrics.heightPixels);
		mMediaRecorder.setOnErrorListener(new OnErrorListener() {							
			@Override
			public void onError(MediaRecorder mr, int what, int extra) {
				LensRocketLogger.e(TAG, "MediaRecorder error");				
			}
		});				
		try {
			mMediaRecorder.prepare();
		} catch (IllegalStateException ex) {
			LensRocketLogger.e(TAG, "IllegalStateException preparing MediaRecorder: " + ex.getMessage());
			releaseMediaRecorder();
			return false;
		} catch (IOException ex) {
			LensRocketLogger.e(TAG, "IOException preparing MediaRecorder: " + ex.getMessage());
			releaseMediaRecorder();
			return false;
		}
		return true;
	}
	
	private OnTouchListener touchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_DOWN) {
	        } else if (event.getAction() == MotionEvent.ACTION_UP) {	        		
	        		if (mTakingVideo) {
	        			LensRocketLogger.i(TAG, "Finished video");
	        			mTakingVideo = false;
	        			mReviewingVideo = true;
	        			try {
		        			mMediaRecorder.stop();		        			
		        			releaseMediaRecorder();	        			
		        			setUIMode(Constants.CameraUIMode.UI_MODE_REVIEW_VIDEO);
	        			} catch (RuntimeException ex) {
	        				LensRocketLogger.e(TAG, "Error stopping media recorder");
	        				LensRocketAlert.showToast(mActivity, R.string.video_recording_failed, true, true);
	        				setUIMode(Constants.CameraUIMode.UI_MODE_PRE_PICTURE);
	        			}
	        		}
	        }
			return false;
		}		
	};
	
	/**
	 * Method to rotate bitmap as found here:
	 * http://stackoverflow.com/questions/13430895/capture-photo-rotate-90-degree-in-samsung-mobile
	 * @param bitmap
	 * @param degrees
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
	    Matrix matrix = new Matrix();
	    if (degrees != 0) {
	        // rotate clockwise
	        matrix.postRotate(degrees, (float) bitmap.getWidth() / 2,
	                (float) bitmap.getHeight() / 2);
	    }
	    try {
	        Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
	                bitmap.getHeight(), matrix, true);
	        if (bitmap != b2) {
	            bitmap.recycle();
	            bitmap = b2;
	        }
	    } catch (OutOfMemoryError ex) {
	        // We have no memory to rotate. Return the original bitmap.
	    }
	    return bitmap;
	}
	
	private PictureCallback mPictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			LensRocketLogger.i(TAG, "pic taken");
			if (mTakingVideo) {
				mReviewingVideo = true;
				setUIMode(Constants.CameraUIMode.UI_MODE_REVIEW_VIDEO);
			}
			else {
				/** 
				 * The Galaxy S3 has an issue where it doens't properly rotate
				 * the photos like other devices do.  This code will check
				 * to see if the current device is an S3 and if so, will
				 * manually rotate the bitmap.  It's a significant amount of 
				 * processing though that should be moved to a background thread
				 * if possible. 
				 **/
				//TODO: try to move this to a background thread
		       if (DeviceDetector.isDeviceS3()) {										
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPreferredConfig = Bitmap.Config.RGB_565;
					options.inScaled = false;				
					Bitmap newTempBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
					if (newTempBitmap.getWidth() > newTempBitmap.getHeight()) {
						LensRocketLogger.e(TAG, "Width greater than height!");
						newTempBitmap = rotateBitmap(newTempBitmap, 90);
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						newTempBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
						data = stream.toByteArray();		
						newTempBitmap.recycle();
					}
		       }
		       /** END OF SAMSUNG GALAXY S3 FIX **/				
				mReviewingPicture = true;
				mPictureData = data;
				setUIMode(Constants.CameraUIMode.UI_MODE_REVIEW_PICTURE);				
				File pictureFile = getOutputMediaFile(Constants.MEDIA_TYPE_IMAGE);
				if (pictureFile == null) {
					LensRocketLogger.d(TAG, "Error creating media file, check storage permissions");
				}
				try {
					FileOutputStream fos = new FileOutputStream(pictureFile);
					fos.write(data);
					fos.close();
				} catch (FileNotFoundException ex) {
					LensRocketLogger.d(TAG, "File not found: " + ex.getMessage());
				} catch (IOException ex) {
					LensRocketLogger.d(TAG, "Error accessing file: " + ex.getMessage());
				}
			}
		}
	};
	
	//This method will be fired if RecordActivity is opened from an intent
	//This helps because it will be called if it's opened a second time from
	//intent which is the case when the user double taps a rocket to reply
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		mIsReply = intent.getBooleanExtra("isReply", false);
		if (mIsReply) {
			mReplyToUserId = intent.getStringExtra("replyToUserId"); 
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		LensRocketLogger.i(TAG, "onResume");		
		int numberOfCams = Camera.getNumberOfCameras();
		mCameraNumber = 0;		
		if (numberOfCams <2 )
			mBtnSwitchCamera.setVisibility(View.GONE);
		else 
			mCameraNumber = PreferencesHandler.GetCameraPreference(getApplicationContext());
		mCamera = getCameraInstance(mCameraNumber);
		//Set picture size to an appropriate size
		List<Size> sizes = mCamera.getParameters().getSupportedPictureSizes();
		for (int i = 0; i < sizes.size(); i++) {
			Size size = sizes.get(i);
			int area = size.width * size.height;
			if (area <= 691200) {
				Parameters params = mCamera.getParameters();
				params.setPictureSize(size.width, size.height);
				mCamera.setParameters(params);
				break;
			}
		}
		
		mCameraPreview = new CameraPreview(this, mCamera);
		mFrameLayout = (FrameLayout) findViewById(R.id.camera_preview);
		mFrameLayout.addView(mCameraPreview);
		mFlashOn = PreferencesHandler.GetFlashPreference(getApplicationContext());
		Camera.Parameters params = mCamera.getParameters();
		List<String> flashModes = params.getSupportedFlashModes();
		params.setRotation(90);
		if (mFlashOn) {
			mBtnFlash.setImageResource(R.drawable.flash_yes);
		} else {
			mBtnFlash.setImageResource(R.drawable.flash_no);
		}
		if (flashModes == null || flashModes.size() == 0) {
			mBtnFlash.setVisibility(View.GONE);
		} else {
			setCameraFlash(params);
		}
		if (mReviewingPicture) {		
			if (mIsSending) {
				mIsSending = false;
				mCameraPreview.setIsReviewing(true);
				if (mBitmapHolder == null || mBitmapHolder.isRecycled()) {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPreferredConfig = Bitmap.Config.RGB_565;
					options.inScaled = false;
					mBitmapHolder = BitmapFactory.decodeByteArray(mPictureData, 0, mPictureData.length, options);
				}
				mImageView.setImageBitmap(mBitmapHolder);
				mImageView.setVisibility(View.VISIBLE);
			}
			//TODO: Set preview to show picture from file path
			LensRocketLogger.i(TAG, "Path: " + mFileFullPath);			
			setUIMode(Constants.CameraUIMode.UI_MODE_REVIEW_PICTURE);
		} else if (mReviewingVideo) {
			mVideoView.setVideoPath(mFileFullPath);
			mVideoView.start();
			mVideoView.setVisibility(View.VISIBLE);
			mCameraPreview.setVisibility(View.GONE);
			setUIMode(Constants.CameraUIMode.UI_MODE_REVIEW_VIDEO);
		} else if (mIsReply){
			setUIMode(Constants.CameraUIMode.UI_MODE_REPLYING);
		} else {
			setUIMode(Constants.CameraUIMode.UI_MODE_PRE_PICTURE);
		}		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.BROADCAST_ROCKETS_UPDATED);
		registerReceiver(receiver, filter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LensRocketLogger.i(TAG, "onPause");
		mCameraPreview.getHolder().removeCallback(mCameraPreview);
		mCamera.release();
		unregisterReceiver(receiver);
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, android.content.Intent intent) {
			if (intent.getAction().equals(Constants.BROADCAST_ROCKETS_UPDATED)) {
				boolean wasSuccess = intent.getBooleanExtra(Constants.ROCKETS_UPDATE_STATUS, false);
				if (wasSuccess) {					
				} else {
					LensRocketAlert.showToast(mActivity, R.string.error_getting_rockets, true, true);
				}				
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	public Camera getCameraInstance(int cameraNumber) {
		Camera camera = null;
		try { 
			camera = Camera.open(cameraNumber);
		} catch (Exception ex) {
			LensRocketLogger.e(TAG, ex.getMessage());
		}
		return camera;
	}
	
	public void tappedFlash(View view) {
		mFlashOn = !mFlashOn;
		PreferencesHandler.SaveFlashPreference(getApplicationContext(), mFlashOn);		
		if (mFlashOn) {
			mBtnFlash.setImageResource(R.drawable.flash_yes);
		} else {
			mBtnFlash.setImageResource(R.drawable.flash_no);
		}
		Camera.Parameters params = mCamera.getParameters();
		setCameraFlash(params);
	}
	
	public void tappedSwitchCamera(View view) {
		mCamera.stopPreview();
		mCamera.release();
		if (mCameraNumber == 0)
			mCameraNumber = 1;
		else
			mCameraNumber = 0;
		mCamera = getCameraInstance(mCameraNumber);
		PreferencesHandler.SaveCameraPreference(getApplicationContext(), mCameraNumber);
		mCameraPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.removeAllViews();
		preview.addView(mCameraPreview);		
		Camera.Parameters params = mCamera.getParameters();
		List<String> flashModes = params.getSupportedFlashModes();
		if (flashModes == null || flashModes.size() == 0) {
			mBtnFlash.setVisibility(View.GONE);
		} else {
			mBtnFlash.setVisibility(View.VISIBLE);
			setCameraFlash(params);
		}
	}
	
	private void setCameraFlash(Camera.Parameters parameters) {
		if (mFlashOn) {
			parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
			mCamera.setParameters(parameters);
		} else {
			parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			mCamera.setParameters(parameters);
		}			
	}
	
	public void tappedRockets(View view) {
		startActivity(new Intent(getApplicationContext(), RocketsListActivity.class));		
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}
	
	public void tappedFriendsList(View view) {
		startActivity(new Intent(getApplicationContext(), FriendsListActivity.class));		
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
	}
	
	public void tappedSend(View view) {
		mIsSending = true;
		Intent intent = new Intent(mActivity, SendToFriendsActivity.class);
		intent.putExtra("filePath", mFileFullPath);
		intent.putExtra("isPicture", mReviewingPicture);
		intent.putExtra("isVideo", mReviewingVideo);
		intent.putExtra("timeToLive", mSecondsSelected);
		intent.putExtra("isReply", mIsReply);
		intent.putExtra("replyToUserId", mReplyToUserId);
		startActivityForResult(intent, Constants.REQUEST_CODE_SEND_TO_FRIENDS);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		LensRocketLogger.d(TAG, "onActivityResult");
		if (requestCode == Constants.REQUEST_CODE_SEND_TO_FRIENDS){
			if (resultCode == Constants.RESULT_CODE_ROCKET_SENT) {
				mImageView.setVisibility(View.GONE);
				setUIMode(CameraUIMode.UI_MODE_PRE_PICTURE);
				Intent intent = new Intent(mActivity, RocketsListActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
				overridePendingTransition(0, 0);
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	public void tappedDelete(View view) {
		if (mBitmapHolder != null && !mBitmapHolder.isRecycled())
			mBitmapHolder.recycle();
		returnToCameraPreview();
	}
	
	public void tappedTime(View view) {
		DialogFragment newFragment = new NumberPickerFragment();
		newFragment.show(getFragmentManager(), "timePicker");
	}
	
	public void setIsScrolling(boolean isScrolling) {
		mIsScrolling = isScrolling;
	}
	
	public boolean getIsScrolling() {
		return mIsScrolling;
	}
	
	public static class NumberPickerFragment extends DialogFragment {		
		@Override
		public void onConfigurationChanged(Configuration newConfig) {
			super.onConfigurationChanged(newConfig);
		}
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			RecordActivity activity = (RecordActivity) getActivity();
			NumberPicker picker = new NumberPicker(activity);
			picker.setMinValue(1);
			picker.setMaxValue(10);
			picker.setWrapSelectorWheel(false);
			picker.setValue(activity.getSelectedSeconds());
			AlertDialog.Builder builder;
			builder = new AlertDialog.Builder(activity);
			builder.setView(picker);
			final AlertDialog dialog = builder.create();
			//Place the dialog at the bottom of the screen
			Window window = dialog.getWindow();			
			WindowManager.LayoutParams wlp = window.getAttributes();
			wlp.gravity = Gravity.BOTTOM;
			window.setAttributes(wlp);			
			//Record if we're scrolling for key down detection logic
			picker.setOnScrollListener(new OnScrollListener() {				
				@Override
				public void onScrollStateChange(NumberPicker view, int scrollState) {
					RecordActivity activity = (RecordActivity) getActivity();
					switch (scrollState) {
					case OnScrollListener.SCROLL_STATE_FLING:
						activity.setIsScrolling(true);
						break;
					case OnScrollListener.SCROLL_STATE_IDLE:
						activity.setIsScrolling(false);
						break;
					case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
						activity.setIsScrolling(true);
						break;
					}					
				}
			});
			picker.setOnValueChangedListener((RecordActivity) getActivity());
			picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
			picker.setDisplayedValues(getResources().getStringArray(R.array.share_seconds_list));
			picker.setOnTouchListener(new OnTouchListener() {				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					RecordActivity activity = (RecordActivity) getActivity();
					if (event.getAction() == MotionEvent.ACTION_UP) {
						if (!activity.getIsScrolling()) {
							//Check to see if they have clicked into the 
							//area specific to the middle row
							//This is a little hacky but NumberPicker's
							//onClickListener doesn't fire as expected
							float ry = event.getY();
							if (ry > 128 && ry < 256) {
								dialog.dismiss();
							}							
						}
					} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
						LensRocketLogger.i("TEST", "down");
					}						
					return false;
				}
			});
			return dialog;			
		}
	}
	
	@Override
	public void onBackPressed() {
		if (mReviewingPicture || mReviewingVideo) {
			returnToCameraPreview();
			if (mBitmapHolder != null && !mBitmapHolder.isRecycled())
				mBitmapHolder.recycle();
		} else { 						
			LensRocketLogger.i(TAG, "back");
			if (mIsReply) {
				LensRocketLogger.i(TAG, "back-reply");
				Intent intent = new Intent(mActivity, RocketsListActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);				
		        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
			} else {			
				LensRocketLogger.i(TAG, "back-finish");
				finish();
			}
		}
	}
	
	private void returnToCameraPreview() {
		if (mReviewingVideo) {
			mVideoView.stopPlayback();
			//Fix for S3 video recorder from here:
			//http://stackoverflow.com/questions/12696318/video-display-is-garbled-when-recording-on-galaxy-s3			
			mCamera.lock();
			mCamera.release();
			mCamera = getCameraInstance(mCameraNumber);
			mCameraPreview = new CameraPreview(this, mCamera);
			mFrameLayout = (FrameLayout) findViewById(R.id.camera_preview);
			mFrameLayout.addView(mCameraPreview);									
			mCameraPreview.setVisibility(View.VISIBLE);
			mVideoView.setVisibility(View.GONE);					
		}
		
		File file = new File(mFileFullPath);
		mFileFullPath = "";
		if (!file.delete()) {
			LensRocketLogger.e(TAG, "Unable to delete file");
		}		
		//Ensure friends won't be checked when we return to them
		mLensRocketService.uncheckFriends();
		mCamera.startPreview();
		mReviewingPicture = false;
		mReviewingVideo = false;
		if (mIsReply) {
			setUIMode(Constants.CameraUIMode.UI_MODE_REPLYING);
		} else {
			setUIMode(Constants.CameraUIMode.UI_MODE_PRE_PICTURE);
		}
	}	

	/** Create a File for saving an image or video */
	private  File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.		
	    mMediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), getResources().getString(R.string.app_name));
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.
	    // Create the storage directory if it does not exist
	    if (! mMediaStorageDir.exists()){
	        if (! mMediaStorageDir.mkdirs()){
	            LensRocketLogger.d(TAG, "failed to create directory");
	            return null;
	        }
	    }
	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == Constants.MEDIA_TYPE_IMAGE){
	    		String imageFileName = "IMG_"+ timeStamp + ".jpg";
	    		mFileFullPath = mMediaStorageDir.getPath() + File.separator +
	    		        imageFileName;
	        mediaFile = new File(mFileFullPath);
	    } else if(type == Constants.MEDIA_TYPE_VIDEO) {
	    		mVideoFileName = "VID_"+ timeStamp + ".mp4";
	    		mFileFullPath = mMediaStorageDir.getPath() + File.separator +
	    		        mVideoFileName;
	        mediaFile = new File(mFileFullPath);	        
	    } else {
	        return null;
	    }
	    return mediaFile;
	}

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		LensRocketLogger.i(TAG, "New value: " + newVal);
		mSecondsSelected = newVal;
		mLblTime.setText(newVal + "");
	}	
	
	public int getSelectedSeconds() {
		return mSecondsSelected;
	}
}
