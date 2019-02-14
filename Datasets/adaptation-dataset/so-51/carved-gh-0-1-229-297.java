public class foo{
	// http://stackoverflow.com/questions/14085199/mp3-to-wav-conversion-in-java
	// TODO - great method .. although why pass in the audio format ???
	public static byte[] convert(byte[] sourceBytes, AudioFormat audioFormat) {
		if (sourceBytes == null || sourceBytes.length == 0 || audioFormat == null) {
			throw new IllegalArgumentException("Illegal Argument passed to this method");
		}

		ByteArrayInputStream bais = null;
		ByteArrayOutputStream baos = null;
		AudioInputStream sourceAIS = null;
		AudioInputStream convert1AIS = null;
		AudioInputStream convert2AIS = null;

		try {
			bais = new ByteArrayInputStream(sourceBytes);
			sourceAIS = AudioSystem.getAudioInputStream(bais);
			AudioFormat sourceFormat = sourceAIS.getFormat();
			AudioFormat convertFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sourceFormat.getSampleRate(), 16, sourceFormat.getChannels(),
					sourceFormat.getChannels() * 2, sourceFormat.getSampleRate(), false);
			convert1AIS = AudioSystem.getAudioInputStream(convertFormat, sourceAIS);
			convert2AIS = AudioSystem.getAudioInputStream(audioFormat, convert1AIS);

			baos = new ByteArrayOutputStream();

			byte[] buffer = new byte[8192];
			while (true) {
				int readCount = convert2AIS.read(buffer, 0, buffer.length);
				if (readCount == -1) {
					break;
				}
				baos.write(buffer, 0, readCount);
			}
			return baos.toByteArray();
		} catch (Exception e) {
			Logging.logError(e);
			return null;
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (Exception e) {
				}
			}
			if (convert2AIS != null) {
				try {
					convert2AIS.close();
				} catch (Exception e) {
				}
			}
			if (convert1AIS != null) {
				try {
					convert1AIS.close();
				} catch (Exception e) {
				}
			}
			if (sourceAIS != null) {
				try {
					sourceAIS.close();
				} catch (Exception e) {
				}
			}
			if (bais != null) {
				try {
					bais.close();
				} catch (Exception e) {
				}
			}
		}
	}
}