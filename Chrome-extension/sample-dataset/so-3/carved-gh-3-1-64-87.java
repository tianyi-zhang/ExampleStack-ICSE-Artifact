public class foo{
    private String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = this.getAssets().open("languages.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
}