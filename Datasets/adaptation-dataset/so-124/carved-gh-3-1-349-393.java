public class foo{
    /**
     * Returns possible external sd card path. Solution taken from here
     * http://stackoverflow.com/a/13648873/527759
     * 
     * @return
     */
    public static Set<String> getExternalMounts() {
        final Set<String> out = new HashSet<String>();
        try {
            String reg = ".*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
            StringBuilder sb = new StringBuilder();
            try {
                final Process process = new ProcessBuilder().command("mount")
                        .redirectErrorStream(true).start();
                process.waitFor();
                final InputStream is = process.getInputStream();
                final byte[] buffer = new byte[1024];
                while (is.read(buffer) != -1) {
                    sb.append(new String(buffer));
                }
                is.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            // parse output
            final String[] lines = sb.toString().split("\n");
            for (String line : lines) {
                if (!line.toLowerCase(Locale.ENGLISH).contains("asec")) {
                    if (line.matches(reg)) {
                        String[] parts = line.split(" ");
                        for (String part : parts) {
                            if (part.startsWith("/"))
                                if (!part.toLowerCase(Locale.ENGLISH).contains("vold")) {
                                    CommonUtils.debug(TAG, "Found path: " + part);
                                    out.add(part);
                                }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            error(TAG, ex);
        }
        return out;
    }
}