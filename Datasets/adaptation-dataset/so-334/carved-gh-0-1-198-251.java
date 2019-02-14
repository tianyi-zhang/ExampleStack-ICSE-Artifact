public class foo{
	// http://stackoverflow.com/questions/13789063/get-sound-from-a-url-with-java
	private void playMP3(final String url) {

		try {

			// Create the JavaFX Panel for the WebView
			JFXPanel fxPanel = new JFXPanel();
			fxPanel.setLocation(new Point(0, 0));

			// Initialize the webView in a JavaFX-Thread
			Platform.runLater(new Runnable() {
				public void run() {
					MediaPlayer player = new MediaPlayer(new Media(url));
					player.play();
				}
			});



			if (true)
				return;

			AudioInputStream in = AudioSystem.getAudioInputStream(new URL(url));
			AudioFormat baseFormat = in.getFormat();
			AudioFormat decodedFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
					baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
					false);
			AudioInputStream din = AudioSystem.getAudioInputStream(
					decodedFormat, in);
			DataLine.Info info = new DataLine.Info(SourceDataLine.class,
					decodedFormat);
			SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
			if (line != null) {
				line.open(decodedFormat);
				byte[] data = new byte[4096];
				// Start
				line.start();

				int nBytesRead;
				while ((nBytesRead = din.read(data, 0, data.length)) != -1) {
					line.write(data, 0, nBytesRead);
				}
				// Stop
				line.drain();
				line.stop();
				line.close();
				din.close();
			}
		} catch (Exception e) {
			Log.debug("playing MP3 failed " + url + " " + e.toString());
		}
	}
}