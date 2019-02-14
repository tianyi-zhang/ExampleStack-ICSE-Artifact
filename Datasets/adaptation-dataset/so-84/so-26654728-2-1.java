public class foo {
public static String sudoForResult(String...strings) {
    String res = "";
    DataOutputStream outputStream = null;
    InputStream response = null;
    try{
        Process su = Runtime.getRuntime().exec("su");
        outputStream = new DataOutputStream(su.getOutputStream());
        response = su.getInputStream();

        for (String s : strings) {
            outputStream.writeBytes(s+"\n");
            outputStream.flush();
        }

        outputStream.writeBytes("exit\n");
        outputStream.flush();
        try {
            su.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        res = readFully(response);
    } catch (IOException e){
        e.printStackTrace();
    } finally {
        Closer.closeSilently(outputStream, response);
    }
    return res;
}
}