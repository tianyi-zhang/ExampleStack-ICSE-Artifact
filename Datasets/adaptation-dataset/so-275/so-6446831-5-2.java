public class foo {
private String getMmsText(String id) {
    Uri partURI = Uri.parse("content://mms/part/" + id);
    InputStream is = null;
    StringBuilder sb = new StringBuilder();
    try {
        is = getContentResolver().openInputStream(partURI);
        if (is != null) {
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader reader = new BufferedReader(isr);
            String temp = reader.readLine();
            while (temp != null) {
                sb.append(temp);
                temp = reader.readLine();
            }
        }
    } catch (IOException e) {}
    finally {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {}
        }
    }
    return sb.toString();
}
}