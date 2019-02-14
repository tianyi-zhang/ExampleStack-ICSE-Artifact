public class foo{
    private static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();

            if (l == 1)
                h = "0" + h;

            if (l > 2)
                h = h.substring(l - 2, l);

            str.append(h.toUpperCase());

            if (i < (arr.length - 1))
                str.append(':');
        }
        return str.toString();
    }
}