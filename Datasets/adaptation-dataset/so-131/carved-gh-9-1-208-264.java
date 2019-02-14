public class foo{
	/**
	 * Extracts the input archive and saves it to the output
	 * 
	 * @param input
	 * @param output
	 * @return
	 */
	public static boolean extractFile(File input, File output) {
		//Modified from:
		//StackOverflow: http://stackoverflow.com/questions/3382996/how-to-unzip-files-programmatically-in-android
		
		ZipInputStream zis;
		
		try {
			String filename;
			zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(input)));
			ZipEntry ze;
			byte[] buffer = new byte[1024];
			int count;
			
			String rootDir = output.getParentFile().getAbsolutePath() + "/";
			
			while((ze = zis.getNextEntry()) != null) {
				filename = ze.getName();
				
				if(isJunkFilename(filename)) {
					zis.closeEntry();
					
					continue;
				}
				
				//Need to create directories if they don't exist, or it will throw an Exception...
				if(ze.isDirectory()) {
					File fmd = new File(rootDir + filename);
					fmd.mkdirs();
					
					continue;
				}
				
				FileOutputStream fout = new FileOutputStream(rootDir + filename);
				
				while((count = zis.read(buffer)) != -1) {
					fout.write(buffer, 0, count);
				}
				
				fout.close();
				zis.closeEntry();
			}
			
			zis.close();
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}