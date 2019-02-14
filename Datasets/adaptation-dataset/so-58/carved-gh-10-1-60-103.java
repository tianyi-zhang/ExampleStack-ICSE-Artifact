public class foo{
	/**
	 * Adds a file to the jar archive specified via the JarOutputStream
	 * @param source The file to be added
	 * @param target An output stream to the jar archive
	 * @param options Options to the compilation process
	 * @throws IOException If an io error occurs
	 */
	private static void addFile(File source, JarOutputStream target, CompilerOptions options) throws IOException{
		BufferedInputStream in = null;
		try{
			if (source.isDirectory()){
				String name = source.getPath().replace(options.tempDirectory + "\\", "").replace("\\", "/");
		    	if (!name.isEmpty()){
		    		if (!name.endsWith("/")) name += "/";
	    			JarEntry entry = new JarEntry(name);
	    			entry.setTime(source.lastModified());
	    			target.putNextEntry(entry);
	    			target.closeEntry();
	    		}
		    	
	    		for (File nestedFile: source.listFiles()){
	    			addFile(nestedFile, target, options);
	    		}
		    }
			else if(!source.getName().endsWith(".java")){
			    JarEntry entry = new JarEntry(source.getPath().replace(options.tempDirectory + "\\", "").replace("\\", "/"));
			    entry.setTime(source.lastModified());
			    target.putNextEntry(entry);
			    in = new BufferedInputStream(new FileInputStream(source));
	
			    byte[] buffer = new byte[1024];
			    while (true){
			    	int count = in.read(buffer);
			    	if (count == -1) break;
			    	
			    	target.write(buffer, 0, count);
			    }
			    target.closeEntry();
			}
		}
		finally{
			if (in != null) in.close();
		}
	}
}