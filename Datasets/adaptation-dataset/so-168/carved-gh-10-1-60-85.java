public class foo{
	/**
	 * https://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java
	 * 
	 * 6 times faster than BufferedReader.readLine()
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static int countLines(File file) throws Exception {
		BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
		byte[] c = new byte[1024];
		int count = 0;
		int readChars = 0;
		boolean empty = true;
		while ((readChars = is.read(c)) != -1) {
			empty = false;
			for (int i = 0; i < readChars; ++i) {
				if (c[i] == '\n') {
					++count;
				}
			}
		}
		is.close();

		return (count == 0 && !empty) ? 1 : count;
	}
}