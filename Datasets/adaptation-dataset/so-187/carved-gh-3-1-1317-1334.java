public class foo{
	//http://stackoverflow.com/questions/16983989/copy-directory-from-assets-to-data-folder
    protected static boolean copyAssetFolder(AssetManager assetManager, String fromAssetPath, String toPath) {
    	try {
    		String[] files = assetManager.list(fromAssetPath);
    		new File(toPath).mkdirs();
    		boolean res = true;
    		for(String file : files)
    			if(file.contains("."))
    				res &= copyAsset(assetManager, fromAssetPath + "/" + file, toPath + "/" + file);
    			else 
    				res &= copyAssetFolder(assetManager, fromAssetPath + "/" + file, toPath + "/" + file);

    		return res;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    }
}