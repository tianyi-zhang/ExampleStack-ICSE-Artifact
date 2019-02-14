public class foo{
		// from http://stackoverflow.com/questions/3712112/search-contact-by-phone-number
		public static String getContactName(Context ctx, String number) {
			
			if (number.equals("")) return "";
			
			Log.d("phone", "looking up " + number + "...");
			
		    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
		    String name = number;

		    ContentResolver contentResolver = ctx.getContentResolver();
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

		    Log.d("phone", "...result is " + name);
		    return name;
		}
}