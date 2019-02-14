public class foo{
    /**
     * Escapes all characters except the following: alphabetic, decimal digits, - _ . ! ~ * ' ( )
     *
     * @param input
     *            A component of a URI
     * @return the escaped URI component
     */
    public static String encodeURIComponent(String input)
    {
        if (input == null)
        {
            return input;
        }

        StringBuilder filtered = new StringBuilder(input.length());
        char c;
        for (int i = 0; i < input.length(); ++i)
        {
            c = input.charAt(i);
            if (dontNeedEncoding.get(c))
            {
                filtered.append(c);
            }
            else
            {
                final byte[] b = charToBytesUTF(c);

                for (int j = 0; j < b.length; ++j)
                {
                    filtered.append('%');
                    filtered.append("0123456789ABCDEF".charAt(b[j] >> 4 & 0xF));
                    filtered.append("0123456789ABCDEF".charAt(b[j] & 0xF));
                }
            }
        }
        return filtered.toString();
    }
}