public class foo {
public static void uncompressTarGZ(File tarFile, File dest) throws IOException {
    dest.mkdir();
    TarArchiveInputStream tarIn = null;

    tarIn = new TarArchiveInputStream(
                new GzipCompressorInputStream(
                    new BufferedInputStream(
                        new FileInputStream(
                            tarFile
                        )
                    )
                )
            );

    TarArchiveEntry tarEntry = tarIn.getNextTarEntry();
    // tarIn is a TarArchiveInputStream
    while (tarEntry != null) {// create a file with the same name as the tarEntry
        File destPath = new File(dest, tarEntry.getName());
        System.out.println("working: " + destPath.getCanonicalPath());
        if (tarEntry.isDirectory()) {
            destPath.mkdirs();
        } else {
            destPath.createNewFile();
            //byte [] btoRead = new byte[(int)tarEntry.getSize()];
            byte [] btoRead = new byte[1024];
            //FileInputStream fin 
            //  = new FileInputStream(destPath.getCanonicalPath());
            BufferedOutputStream bout = 
                new BufferedOutputStream(new FileOutputStream(destPath));
            int len = 0;

            while((len = tarIn.read(btoRead)) != -1)
            {
                bout.write(btoRead,0,len);
            }

            bout.close();
            btoRead = null;

        }
        tarEntry = tarIn.getNextTarEntry();
    }
    tarIn.close();
}
}