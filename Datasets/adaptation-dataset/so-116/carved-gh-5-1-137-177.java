public class foo{
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
}