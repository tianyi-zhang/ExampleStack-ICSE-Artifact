public class foo{
    //exec any number of commands as su
    //Source: https://stackoverflow.com/questions/20932102/execute-shell-command-from-android
    public static String execSuCommand(String... strings) {
        String res = "";
        DataOutputStream outputStream = null;
        InputStream response = null;
        try {
            Process su = Runtime.getRuntime().exec("su");
            outputStream = new DataOutputStream(su.getOutputStream());
            response = su.getInputStream();
            //any arguments
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
            System.out.println(res);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Closer.closeSilently(outputStream, response);
        }

        return res;
    }
}