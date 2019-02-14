package com.poinsart.votar;

import java.io.File;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

public class PathFromUri {
	// based on Neil on stackoverflow : http://stackoverflow.com/a/28453016

	// modified for more compatibility with different file managers
	// i.e. : es explorer return a media:// path, gira returns a file:// path...

	/**
	 * Gets the real path from file
	 * 
	 * @param context
	 * @param contentUri
	 * @return path
	 */
	public static String getRealPathFromURI(Context context, Uri contentUri) {
		String filePath = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			filePath = getPathForV19AndUp(context, contentUri);
		}
		if (filePath == null) {
			filePath = getPathForPreV19(context, contentUri);
		}
		return filePath;
	}

	/**
	 * Handles pre V19 uri's
	 * 
	 * @param context
	 * @param contentUri
	 * @return
	 */
	protected static String getPathForPreV19(Context context, Uri contentUri) {
		String res = null;

		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				res = cursor.getString(column_index);
			}
			cursor.close();
		}
		if (res == null || !(new File(res).canRead())) {
			File file = new File(contentUri.getPath());
			if (file != null) {
				res = file.getAbsolutePath();
			}
		}

		return res;
	}

	/**
	 * Handles V19 and up uri's
	 * 
	 * @param context
	 * @param contentUri
	 * @return path
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	protected static String getPathForV19AndUp(Context context, Uri contentUri) {
		if (!DocumentsContract.isDocumentUri(context, contentUri)) {
			return null;
		}
		String wholeID = DocumentsContract.getDocumentId(contentUri);
		if (wholeID == null) {
			return null;
		}

		// Split at colon, use second item in the array
		String id = wholeID.split(":")[1];
		String[] column = { MediaStore.Images.Media.DATA };

		// where id is equal to
		String sel = MediaStore.Images.Media._ID + "=?";
		Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel,
				new String[] { id }, null);
		if (cursor == null)
			return null;

		String filePath = "";
		int columnIndex = cursor.getColumnIndex(column[0]);
		if (cursor.moveToFirst()) {
			filePath = cursor.getString(columnIndex);
		}

		cursor.close();
		return filePath;
	}
}
