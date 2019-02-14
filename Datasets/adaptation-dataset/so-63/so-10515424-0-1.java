public class foo {
public void DownloadFiles(){

    try {
        URL url = new URL("http://nodeload.github.com/nexes/Android-File-Manager/zipball/master");
        URLConnection conexion = url.openConnection();
        conexion.connect();
        int lenghtOfFile = conexion.getContentLength();
        InputStream is = url.openStream();
        File testDirectory = new File(Environment.getExternalStorageDirectory() + "/Folder");
        if (!testDirectory.exists()) {
            testDirectory.mkdir();
        }
        FileOutputStream fos = new FileOutputStream(testDirectory + "/zip.zip");
        byte data[] = new byte[1024];
        int count = 0;
        long total = 0;
        int progress = 0;
        while ((count = is.read(data)) != -1) {
            total += count;
            int progress_temp = (int) total * 100 / lenghtOfFile;
            if (progress_temp % 10 == 0 && progress != progress_temp) {
                progress = progress_temp;
            }
            fos.write(data, 0, count);
        }
        is.close();
        fos.close();
    } catch (MalformedURLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
}
}