public class foo{
    // lifted from http://stackoverflow.com/a/23952928/3214339
    private String loadAssetAsString(String path) {
        StringBuilder buffer = new StringBuilder();
        InputStream in = null;
        try {
            in = assets.open(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String str;
            boolean isFirst = true;
            while ((str = reader.readLine()) != null) {
                if (isFirst)
                    isFirst = false;
                else
                    buffer.append('\n');
                buffer.append(str);
            }
        } catch (IOException e) {
            Log.d(CampusMapActivity.class.getSimpleName(), "Error opening asset");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.d(CampusMapActivity.class.getSimpleName(), "Error closing asset");
                }
            }
        }
        return buffer.toString();
    }
}