public class foo{
	public void handleMediaKeyEvent(KeyEvent keyEvent) {

		//This method provided by user1937272@StackOverflow
		//http://stackoverflow.com/questions/12573442/is-google-play-music-hogging-all-action-media-button-intents
		//Sends the keyevent to app currently playing media

		/*
		 * Attempt to execute the following with reflection. 
		 * 
		 * [Code]
		 * IAudioService audioService = IAudioService.Stub.asInterface(b);
		 * audioService.dispatchMediaKeyEvent(keyEvent);
		 */

		try {

			// Get binder from ServiceManager.checkService(String)
			IBinder iBinder  = (IBinder) Class.forName("android.os.ServiceManager")
					.getDeclaredMethod("checkService",String.class)
					.invoke(null, Context.AUDIO_SERVICE);

			// get audioService from IAudioService.Stub.asInterface(IBinder)
			Object audioService  = Class.forName("android.media.IAudioService$Stub")
					.getDeclaredMethod("asInterface",IBinder.class)
					.invoke(null,iBinder);

			// Dispatch keyEvent using IAudioService.dispatchMediaKeyEvent(KeyEvent)
			Class.forName("android.media.IAudioService")
			.getDeclaredMethod("dispatchMediaKeyEvent",KeyEvent.class)
			.invoke(audioService, keyEvent);            

		}  catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}