public class foo{
	protected static Bitmap bitmapForUri(Context context, Uri imageUri) throws IOException 
	{
		if (imageUri == null)
		{
			Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);

			return b;
		}
		
		InputStream input = PurpleWidgetProvider.inputStreamForUri(context, imageUri);
		
		if (input == null)
			return null;

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > 144) ? (originalSize / 144) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

        bitmapOptions.inSampleSize = PurpleWidgetProvider.getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither=true; 
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional

		input = PurpleWidgetProvider.inputStreamForUri(context,  imageUri);
		
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return bitmap;
	}
}