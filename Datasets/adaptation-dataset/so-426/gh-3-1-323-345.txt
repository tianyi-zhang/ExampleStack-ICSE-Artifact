/*
    VotAR : Vote with Augmented reality
    Copyright (C) 2013 Stephane Poinsart <s@poinsart.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.poinsart.votar;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.CountDownLatch;
import java.lang.System;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

import org.json.JSONArray;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.media.ExifInterface;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class VotarMain extends Activity {
	public static final int MEDIA_TYPE_IMAGE = 1;

	private Uri cameraFileUri;
	public String lastPhotoFilePath=null;
	public String lastPointsJsonString=null;

	/** Create a file Uri for saving an image or video */

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private static final int CAMERA_REQUEST=1;
	private static final int GALLERY_REQUEST=2;
	
	private static final int CAMERA_PERMISSION=1;
	private static final int STORAGE_PERMISSION=2;
	
	private ImageView imageView;
	private ProgressBar bar[]= {null, null, null, null};
	private TextView barLabel[]={null, null, null, null};
	public TextView wifiLabel=null, introLabel=null;
	private LinearLayout mainLayout, controlLayout;
	private RelativeLayout relativeLayout;
	
	public CountDownLatch photoLock;
	public CountDownLatch pointsLock;
	
	private static final long TIME_DIVIDE=100000;
	public long datatimestamp=Long.MIN_VALUE/TIME_DIVIDE;
	
	public BitmapFactory.Options opt=null;
	
	private VotarWebServer votarwebserver;
	public AssetManager assetMgr;
	
	public void errormsg(String message) {
		Log.w("VotAR error", message);
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

		dlgAlert.setMessage(message);
		dlgAlert.setTitle(getString(R.string.error_title));
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();

        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    System.loadLibrary("VotAR");
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		assetMgr = this.getAssets();
		
		imageView = (ImageView) findViewById(R.id.imageView);
		bar[0]=(ProgressBar) findViewById(R.id.bar_a);
		bar[1]=(ProgressBar) findViewById(R.id.bar_b);
		bar[2]=(ProgressBar) findViewById(R.id.bar_c);
		bar[3]=(ProgressBar) findViewById(R.id.bar_d);
		
		barLabel[0]=(TextView) findViewById(R.id.label_a);
		barLabel[1]=(TextView) findViewById(R.id.label_b);
		barLabel[2]=(TextView) findViewById(R.id.label_c);
		barLabel[3]=(TextView) findViewById(R.id.label_d);
		
		wifiLabel=(TextView) findViewById(R.id.label_wifi);
		introLabel=((TextView)findViewById(R.id.introLabel));
		
		mainLayout=((LinearLayout)findViewById(R.id.mainLayout));
		controlLayout=((LinearLayout)findViewById(R.id.controlLayout));
		relativeLayout=((RelativeLayout)findViewById(R.id.relativeLayout));
		
		
		adjustLayoutForOrientation(getResources().getConfiguration().orientation);
		
		findViewById(R.id.introLabel).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://votar.libre-innovation.org/help/"));
				startActivity(browserIntent);
			}
		});

		findViewById(R.id.buttonCamera).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Build.VERSION.SDK_INT<23) {
					startCamera();
				} else {
					a60Camera();
				}
			}
		});
		findViewById(R.id.buttonGallery).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Build.VERSION.SDK_INT<23) {
					startGallery();
				} else {
					a60ExtStorage();
				}
			}
		});
		votarwebserver=new VotarWebServer(51285, this);
		try {
			votarwebserver.start();
		} catch (IOException e) {
			this.errormsg(getString(R.string.error_nowebserver));
		}
	}
	
	void a60Camera() {
		if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
		} else {
			startCamera();
		}
	}
	
	void a60ExtStorage() {
		if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
			// android >= 6 but permission already granted, so nothing more to try, fail
			requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
		} else {
			startGallery();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (votarwebserver!=null)
			votarwebserver.stop();
	}
	
	@Override
	public void onBackPressed() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(getString(R.string.quit_title));
		alert.setMessage(getString(R.string.quit_message));
		
		alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
				finish();
			}
		});

		alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//
			}
		});

		alert.show();
	}
	
	private void startCamera() {
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		if (cameraIntent.resolveActivity(getPackageManager()) == null) {
			VotarMain.this.errormsg(getString(R.string.error_photoapplaunch));
			return;
		}
		File media=getOutputMediaFile(MEDIA_TYPE_IMAGE);
		if (media==null) {
			VotarMain.this.errormsg(getString(R.string.error_extstorage));
			return;
		}

	    cameraFileUri = Uri.fromFile(media); // create a file to save the image
	    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri); // set the image file name

		startActivityForResult(cameraIntent, CAMERA_REQUEST);
	}
	
	private void startGallery() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST);
	}
	
	private void adjustLayoutForOrientation(int orientation) {
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			mainLayout.setOrientation(LinearLayout.HORIZONTAL);
			controlLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT,1));
		} else if (orientation == Configuration.ORIENTATION_PORTRAIT){
			mainLayout.setOrientation(LinearLayout.VERTICAL);
			controlLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,1));
		}
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		adjustLayoutForOrientation(newConfig.orientation);
	}
	
	/** Create a File for saving an image or video */
	@SuppressLint("SimpleDateFormat")
	private File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir;
		
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			this.errormsg(getString(R.string.error_extstorage));
			return null;
		}
		
		if (Build.VERSION.SDK_INT<23) {
			// This location works best if you want the created images to be shared
			// between applications and persist after your app has been uninstalled.
			mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_PICTURES), "VotAR");
		} else {
			mediaStorageDir = new File(getExternalFilesDir(null), "VotAR");
		}
		
		// Create the storage directory if it does not exist
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				this.errormsg(getString(R.string.error_createphotodir));
				return null;
			}
		}


	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	
	// mostly based on stackoverflow answer from ajma :
	// http://stackoverflow.com/questions/9573196/how-to-get-the-ip-of-the-wifi-hotspot-in-android
    public String getWifiIp() {
    	Enumeration<NetworkInterface> en=null;
    	try {
    		 en = NetworkInterface.getNetworkInterfaces();
    	} catch (SocketException e) {
    		return null;
    	}
		while (en.hasMoreElements()) {
			NetworkInterface intf = en.nextElement();
			if (intf.getName().contains("wlan")) {
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && (inetAddress.getAddress().length == 4)) {
						//Log.d("VotAR Main", inetAddress.getHostAddress());
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		}
		return null;
    }

    public boolean updateWifiStatus() {
    	String wifiIp=getWifiIp();
		if (wifiIp!=null) {
			wifiLabel.setText("http://"+wifiIp+":51285");
			return true;
		}
		wifiLabel.setText(getString(R.string.nowificon));
		return false;
    }
    
    @Override
	protected void onResume()
	{
    	super.onResume();
    	IntentFilter intentFilter = new IntentFilter();
    	intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    	registerReceiver(broadcastReceiver, intentFilter);
    	updateWifiStatus();
    }

    @Override
    protected void onPause()
    {
    	super.onPause();
    	unregisterReceiver(broadcastReceiver);
    }
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		final String action = intent.getAction();
    		if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
    			updateWifiStatus();
    		}
    	}
	};
	

	
	private class AnalyzeTask extends AsyncTask<Bitmap, Integer, Void> {
		private AnalyzeReturn ar=new AnalyzeReturn();
		
		private ProgressDialog mProgressDialog;
		
		// found in native code, check jni exports
		public native boolean nativeAnalyze(AnalyzeReturn ar);
		
		@Override
		protected Void doInBackground(Bitmap... photos) {
			// data is being updated and not ready for HTTP service, lock them
			photoLock=new CountDownLatch(1);
			pointsLock=new CountDownLatch(1);
			datatimestamp=System.nanoTime()/TIME_DIVIDE;
			
			opt.inJustDecodeBounds = true;
		    BitmapFactory.decodeFile(lastPhotoFilePath, opt);
		    opt.inSampleSize=computeSampleSize(opt.outWidth, opt.outHeight);
		    
		    opt.inJustDecodeBounds = false;
			ar.photo=BitmapFactory.decodeFile(lastPhotoFilePath, opt);
			if (ar.photo==null)
				return null;
			
			// handle orientation changes in photos
			// inspired by MKJParekh answer on stackoverflow
			// http://stackoverflow.com/questions/12299963/facing-orientation-issue-with-camera-captured-image-on-android-phones
	        ExifInterface exif;
			try {
				exif = new ExifInterface(lastPhotoFilePath);
			} catch (IOException e) {
				// invalid photo format ?
				return null;
			}
	        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
	        Log.e("orientation", "" + orientation);
	        
	        Matrix m = new Matrix();
	        if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
	            m.postRotate(180);
	            Log.i("VotAR Main", "Orientation change: 70");
	            ar.photo = Bitmap.createBitmap(ar.photo, 0, 0, ar.photo.getWidth(), ar.photo.getHeight(), m, true);
	        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
	            m.postRotate(90);
	            Log.i("VotAR Main", "Orientation change: 90");
	            ar.photo = Bitmap.createBitmap(ar.photo, 0, 0, ar.photo.getWidth(), ar.photo.getHeight(), m, true);
	        }
	        else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
	        	m.postRotate(270);
	            Log.i("VotAR Main", "Orientation change: 270");
	            ar.photo = Bitmap.createBitmap(ar.photo, 0, 0, ar.photo.getWidth(), ar.photo.getHeight(), m, true);
	        }
			
			photoLock.countDown();
			nativeAnalyze(ar);
			return null;
		}
		
		private void showProgressDialog(int step) {
			switch (step) {
			case 0:
				mProgressDialog = new ProgressDialog(VotarMain.this);
				mProgressDialog.setTitle(getString(R.string.title_processing_1));
				//mProgressDialog.setProgressNumberFormat("%1d%%"); // not working on API level 9
				mProgressDialog.setMax(100);
				mProgressDialog.setProgress(0);
				mProgressDialog.setIndeterminate(false);
				mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				mProgressDialog.setMessage(getString(R.string.processing_1));
				mProgressDialog.show();
				break;
			case 1:
				mProgressDialog.setProgress(20);
				float mpSize=(float)ar.photo.getWidth()*ar.photo.getHeight()/1000000;
				mProgressDialog.setTitle(getString(R.string.title_processing_2)+new DecimalFormat("#.#").format(mpSize)+"mp");
				mProgressDialog.setMessage(getString(R.string.processing_2));
				break;
			case 2:
				mProgressDialog.setProgress(40);
				mProgressDialog.setMessage(getString(R.string.processing_3));
				break;
			case 3:
				mProgressDialog.setProgress(90);
				mProgressDialog.setMessage(getString(R.string.processing_4));
				break;
			case 4:
				mProgressDialog.dismiss();
				break;
			}
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			showProgressDialog(progress[0]);
		}
		
		protected void onPreExecute() {
			showProgressDialog(0);
		}
		
		@Override
		protected void onPostExecute(Void unused) {
			int[] prcount=ar.prcount;
			Bitmap photo=ar.photo;
			if (photo==null) {
				mProgressDialog.dismiss();
				VotarMain.this.errormsg(getString(R.string.error_nobitmapdata));
				return;
			}
			// nativeAnalyze returns null if anything goes wrong, just silently ignore
			if (prcount!=null && ar.mark!=null) {
				int max=0;
				for (int i=0; i<4; i++) {
					if (prcount[i]>max)
						max=prcount[i];
					barLabel[i].setText(new String(Character.toChars(97+i))+": "+prcount[i]);
				}
				for (int i=0; i<4; i++) {
					bar[i].setMax(max);
					bar[i].setProgress(prcount[i]);
				}

				writeJsonPoints(ar.mark);
				
				if (photo.getWidth()>relativeLayout.getWidth() && photo.getHeight()>relativeLayout.getHeight()) {
					int maxWidth, maxHeight;
					if (((float) photo.getWidth() / relativeLayout.getWidth()) > ((float) photo.getHeight() / relativeLayout.getHeight())) {
						// photo is large, limit to imageView width, preserve aspect ratio
						maxWidth=relativeLayout.getWidth();
						maxHeight=photo.getHeight()*relativeLayout.getWidth()/photo.getWidth();
					} else {
						// photo is high, limit to imageview height, preserve aspect ratio
						maxHeight=relativeLayout.getHeight();
						maxWidth=photo.getWidth()*relativeLayout.getHeight()/photo.getHeight();
					}
					Log.i("VotAR Main","Image resized for display: "+photo.getWidth()+"x"+photo.getHeight()
							+" -> "+maxWidth+"x"+maxHeight+" [in "+relativeLayout.getWidth()+"x"+relativeLayout.getHeight()+"]");
					photo=Bitmap.createScaledBitmap(photo, maxWidth, maxHeight, true);
				}
				
				introLabel.setVisibility(View.GONE);
				imageView.setImageBitmap(photo);
			}


			pointsLock.countDown();
			showProgressDialog(4);
	    }
	}

	/*
	 *  for now this just save the points into a json string,
	 *  could use a file for more permanent storage
	 */
	private void writeJsonPoints(Mark mark[]) {
		if (mark==null)
			return;
		JSONArray jsonmark=new JSONArray();
		for (int i=0; i<mark.length; i++) {
			JSONArray jsoncurrentmark=new JSONArray(Arrays.asList(new Integer[]{mark[i].x*opt.inSampleSize, mark[i].y*opt.inSampleSize, mark[i].pr}));
			jsonmark.put(jsoncurrentmark);
		}
		lastPointsJsonString=jsonmark.toString();
	}
	
	private int computeSampleSize(int w, int h) {
		int pixelCount=w*h;
		int allowedPixelCount;
		long maxMemory = Runtime.getRuntime().maxMemory();
		
		// large memory devices are allowed bigger images
		if (maxMemory<48000000) {
			allowedPixelCount=4000000;
		} else if (maxMemory<96000000) {
			allowedPixelCount=5500000;
		} else if (maxMemory<128000000) {
			allowedPixelCount=9000000;
		} else {
			allowedPixelCount=24000000;
		}
		
		if (pixelCount<=allowedPixelCount) {
			return 1;
		}
		
		// if the image is too large for processing
		// we divide it's dimensions by 2,
		// so it's pixelCount goes down 4, should be enough
		return 2;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Uri uri=null;
		String newPhotoFilePath=null;
		
		
		if (resultCode == RESULT_CANCELED ) {
			return;
		}
		if (resultCode != RESULT_OK ) {
			errormsg(getString(R.string.error_activityresult));
			return;
		}
		
		if (requestCode == GALLERY_REQUEST) {
			if (data==null || data.getData()==null) {
	        	errormsg(getString(R.string.error_gallerydata));
	        	return;
			}
	        uri=data.getData();
	        if (uri==null) {
	        	errormsg(getString(R.string.error_galleryuri));
	        	return;
	        }
	        newPhotoFilePath=PathFromUri.getRealPathFromURI(this, uri);
	        if (newPhotoFilePath==null) {
	        	newPhotoFilePath=uri.getPath();
	        }
		}
		if (requestCode == CAMERA_REQUEST) {
	 		uri = cameraFileUri;
	 		if (uri==null) {
	 			this.errormsg(getString(R.string.error_nocamerareturn));
	 			return;
	 		}
	 		newPhotoFilePath=uri.getPath();
		}
		
		if (newPhotoFilePath==null) {
			this.errormsg(getString(R.string.error_nocamerareturn2));
			return;
		}

		
		lastPhotoFilePath=newPhotoFilePath;
		lastPointsJsonString=null;
		
		if (!new File(newPhotoFilePath).canRead()) {
			this.errormsg(getString(R.string.error_filepermission));
			return;
		} else {
			Bitmap photo=null;
			opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
			
			new AnalyzeTask().execute(photo);
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
		switch(permsRequestCode) {
		case STORAGE_PERMISSION:
			if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				this.errormsg(getString(R.string.error_filepermission));
			} else {
				startGallery();
			}
			break;
		case CAMERA_PERMISSION:
			if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				this.errormsg(getString(R.string.error_camerapermission));
			} else {
				startCamera();
			}
			break;
		}
	}
}
