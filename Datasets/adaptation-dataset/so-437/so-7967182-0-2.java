public class foo {
public String getContactDisplayNameByNumber(String number) {
    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
    String name = "?";

    ContentResolver contentResolver = getContentResolver();
    Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
            ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

    try {
        if (contactLookup != null && contactLookup.getCount() > 0) {
            contactLookup.moveToNext();
            name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
        }
    } finally {
        if (contactLookup != null) {
            contactLookup.close();
        }
    }

    return name;
}
}