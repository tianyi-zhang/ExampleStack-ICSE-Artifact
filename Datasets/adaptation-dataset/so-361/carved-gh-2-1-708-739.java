public class foo{
		/* Intent createEmailOnlyChooserIntent from Stack Overflow:
		 * 
		 * http://stackoverflow.com/questions/2197741/how-to-send-email-from-my-android-application/12804063#12804063
		 * 
		 * Q: http://stackoverflow.com/users/138030/rakesh
		 * A: http://stackoverflow.com/users/1473663/nobu-games
		 */
		public Intent createEmailOnlyChooserIntent(Intent source, CharSequence chooserTitle) {
			BugSenseHandler.leaveBreadcrumb("createEmailOnlyChooserIntent");
			Stack<Intent> intents = new Stack<Intent>();
	        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
	        		"info@domain.com", null));
	        List<ResolveInfo> activities = getActivity().getPackageManager()
	                .queryIntentActivities(i, 0);

	        for(ResolveInfo ri : activities) {
	            Intent target = new Intent(source);
	            target.setPackage(ri.activityInfo.packageName);
	            intents.add(target);
	        }

	        if(!intents.isEmpty()) {
	            Intent chooserIntent = Intent.createChooser(intents.remove(0),
	                    chooserTitle);
	            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
	                    intents.toArray(new Parcelable[intents.size()]));

	            return chooserIntent;
	        } else {
	        	return Intent.createChooser(source, chooserTitle);
	        }
		}
}