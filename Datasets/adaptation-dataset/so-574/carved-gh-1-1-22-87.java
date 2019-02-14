public class foo{
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
			Parcelable parcelable = event.getParcelableData();

			if (parcelable instanceof Notification) {
				// Statusbar Notification

				Notification notification = (Notification) parcelable;
				RemoteViews views = notification.contentView;
				
				// very hacky shit to read the text of the notification.
				// http://stackoverflow.com/questions/9292032/extract-notification-text-from-parcelable-contentview-or-contentintent
			    Class secretClass = views.getClass();
				try {
					Map<Integer, String> text = new HashMap<Integer, String>();

					Field outerFields[] = secretClass.getDeclaredFields();
					for (int i = 0; i < outerFields.length; i++) {
						if (!outerFields[i].getName().equals("mActions"))
							continue;

						outerFields[i].setAccessible(true);

						ArrayList<Object> actions = (ArrayList<Object>) outerFields[i]
								.get(views);
						for (Object action : actions) {
							Field innerFields[] = action.getClass()
									.getDeclaredFields();

							Object value = null;
							Integer type = null;
							Integer viewId = null;
							for (Field field : innerFields) {
								field.setAccessible(true);
								if (field.getName().equals("value")) {
									value = field.get(action);
								} else if (field.getName().equals("type")) {
									type = field.getInt(action);
								} else if (field.getName().equals("viewId")) {
									viewId = field.getInt(action);
								}
							}

							if (type == 9 || type == 10) {
								text.put(viewId, value.toString());
							}
						}

//						Log.d(TAG, "title is: "+text.get(16908310));
//						Log.d(TAG, "title is: " + text.get(16908310));
//						Log.d(TAG, "info is: " + text.get(16909082));
						String notificationText = text.get(16908358);
						Log.d(TAG, "text is: " + notificationText);
						if (notificationText.contains("Connecting")) {
							Intent lockIntent = new Intent("com.getpebble.action.PEBBLE_DISCONNECTED");
							lockIntent.putExtra(PebbleUnlockReceiver.EXTRA_LOST_CONNECTION, true);
							sendBroadcast(lockIntent);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}