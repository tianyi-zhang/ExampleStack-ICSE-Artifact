package io.vcdroidkit.controllers;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Camera/gallery chooser: http://stackoverflow.com/a/12347567/1449965
// Photo path: http://stackoverflow.com/a/27293655/1449965

public class ImagePickerController extends ViewController
{
	public interface Listener
	{
		void onPickedImage(ImageInfo imageInfo);
		void onCancel();
	}

	public class ImageInfo implements Closeable
	{
		private Uri uri;
		private File tempFile;

		public ImageInfo()
		{
		}

		@Override
		public void close()
		{
			if(tempFile == null)
				return;

			tempFile.delete();
			tempFile = null;
		}

		public Uri getUri()
		{
			return uri;
		}
	} // ImageInfo

	private boolean appeared = false;
	private boolean appearedAnimated;

	private int REQUEST_TAKE_PHOTO = 5322;
	private int REQUEST_SHOW_GALLERY = 5322;

	private Listener listener;
	private File photoFile;
	private Uri photoFileUri;

	public ImagePickerController(AppCompatActivity activity, Listener listener)
	{
		super(activity);
		setOpaque(false);
		this.listener = listener;
	}

	@Override
	public void onViewDidAppear(boolean animated)
	{
		super.onViewDidAppear(animated);
		appearedAnimated = animated;

		if(appeared)
			return;

		appeared = true;

		boolean success;

		success = showChooser();
		//success = takePhoto();
		//success = showGallery();

		if(!success)
		{
			new Handler(Looper.getMainLooper()).post(new Runnable()
			{
				@Override
				public void run()
				{
					dismissController(null, appearedAnimated, new Runnable()
					{
						@Override
						public void run()
						{
							if(listener != null)
								listener.onCancel();
						}
					});
				}
			});

			return;
		}
	} // onViewDidAppear

	private boolean prepareFile()
	{
		final String dirPath = getActivity().getExternalCacheDir().getAbsolutePath();
		File dir = new File(dirPath);
		dir.mkdirs();

		try
		{
			photoFile = File.createTempFile("photo", ".jpg", dir);
		}
		catch (IOException ex)
		{
			return false;
		}

		photoFileUri = Uri.fromFile(photoFile);
		return true;
	} // prepareFile

	private boolean showChooser()
	{
		if(!prepareFile())
			return false;

		// Camera.
		final List<Intent> cameraIntents = new ArrayList<>();
		final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		final PackageManager packageManager = getActivity().getPackageManager();
		final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
		for(ResolveInfo res : listCam)
		{
			final String packageName = res.activityInfo.packageName;
			final Intent intent = new Intent(captureIntent);
			intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
			intent.setPackage(packageName);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri);
			intent.putExtra("imagePicker_cameraCapture", true);
			cameraIntents.add(intent);
		}

		// Filesystem.
		final Intent docsIntent = new Intent(Intent.ACTION_GET_CONTENT);
		docsIntent.setType("image/*");

		final Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		galleryIntent.setType("image/*");
		cameraIntents.add(galleryIntent);

		// Chooser of filesystem options.
		final Intent chooserIntent = Intent.createChooser(docsIntent, getTitle());

		// Add the camera options.
		chooserIntent.putExtra(
				Intent.EXTRA_INITIAL_INTENTS,
				cameraIntents.toArray(new Parcelable[cameraIntents.size()])
		);

		startActivityForResult(chooserIntent, REQUEST_TAKE_PHOTO);
		return true;
	}

	private boolean takePhoto()
	{
		if(!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
		{
			//Logger.error("Camera not found on this device.");
			return false;
		}

		if(!prepareFile())
			return false;

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri);

		if (intent.resolveActivity(getActivity().getPackageManager()) == null)
		{
			//Logger.error("Take photo intent cannot be handled.");
			return false;
		}

		startActivityForResult(intent, REQUEST_TAKE_PHOTO);
		return true;
	} // takePhoto

	private boolean showGallery()
	{
		Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
		getIntent.setType("image/*");

		/*Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		pickIntent.setType("image/*");

		Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});*/

		startActivityForResult(getIntent, REQUEST_SHOW_GALLERY);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		super.onActivityResult(requestCode, resultCode, intent);

		if(requestCode != REQUEST_TAKE_PHOTO && requestCode != REQUEST_SHOW_GALLERY)
			return;

		if(resultCode != Activity.RESULT_OK)
		{
			//Logger.error("onActivityResult failed with code {}", resultCode);

			dismissController(null, appearedAnimated, new Runnable()
			{
				@Override
				public void run()
				{
					if(listener != null)
						listener.onCancel();
				}
			});

			return;
		}

		final ImageInfo imageInfo = new ImageInfo();

		boolean isCamera;
		if (intent == null) {
			isCamera = true;
		} else {
			final String action = intent.getAction();
			if (action == null) {
				isCamera = false;
			} else {
				isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			}
		}

		if(photoFile.length() != 0)
			isCamera = true;

		if(isCamera)
		{
			imageInfo.uri = photoFileUri;
			imageInfo.tempFile = photoFile;
		}
		else
		{
			imageInfo.uri = intent == null ? null : intent.getData();
			photoFile.delete();
		}

		dismissController(null, appearedAnimated, new Runnable()
		{
			@Override
			public void run()
			{
				if(imageInfo.uri == null)
				{
					if (listener != null)
						listener.onCancel();

					return;
				}

				if (listener != null)
					listener.onPickedImage(imageInfo);
			}
		});
	} // onActivityResult
} // ImagePickerController
