public class foo {
public static String dictFormat(String format, Hashtable<String, Object> values) {
    StringBuilder convFormat = new StringBuilder(format);
    Enumeration<String> keys = values.keys();
    ArrayList valueList = new ArrayList();
    int currentPos = 1;
    while (keys.hasMoreElements()) {
        String key = keys.nextElement(),
        formatKey = "%(" + key + ")",
        formatPos = "%" + Integer.toString(currentPos) + "$";
        int index = -1;
        while ((index = convFormat.indexOf(formatKey, index)) != -1) {
            convFormat.replace(index, index + formatKey.length(), formatPos);
            index += formatPos.length();
        }
        valueList.add(values.get(key));
        ++currentPos;
    }
    return String.format(convFormat.toString(), valueList.toArray());
}
}