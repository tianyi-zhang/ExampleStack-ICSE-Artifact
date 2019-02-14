public class foo {
/**
 * Handles V19 and up uri's
 * @param context
 * @param contentUri
 * @return path
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public static String getPathForV19AndUp(Context context, Uri contentUri) {
    String wholeID = DocumentsContract.getDocumentId(contentUri);

    // Split at colon, use second item in the array
    String id = wholeID.split(":")[1];
    String[] column = { MediaStore.Images.Media.DATA };

    // where id is equal to
    String sel = MediaStore.Images.Media._ID + "=?";
    Cursor cursor = context.getContentResolver().
            query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{ id }, null);

    String filePath = "";
    int columnIndex = cursor.getColumnIndex(column[0]);
    if (cursor.moveToFirst()) {
        filePath = cursor.getString(columnIndex);
    }

    cursor.close();
    return filePath;
}
}