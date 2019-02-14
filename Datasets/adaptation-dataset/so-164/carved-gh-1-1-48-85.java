public class foo{
	private byte[] readTileImage(int x, int y, int zoom) {
		FileInputStream in = null;
		ByteArrayOutputStream buffer = null;

		try {
			String patch = DirectoryPath.getMapsPath() + getTileFilename(x, y, zoom);
			in = new FileInputStream(patch);
			buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] data = new byte[BUFFER_SIZE];

			while ((nRead = in.read(data, 0, BUFFER_SIZE)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();

			return buffer.toByteArray();
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (Exception ignored) {
				}
			if (buffer != null)
				try {
					buffer.close();
				} catch (Exception ignored) {
				}
		}
	}
}