public class foo {
public static String BytesNumberToHumanReadableString(long bytes, bool SI1000orBinary1024)
    {

        int unit = SI1000orBinary1024 ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int)(Math.Log(bytes) / Math.Log(unit));
        String pre = (SI1000orBinary1024 ? "kMGTPE" : "KMGTPE")[(exp - 1)] + (SI1000orBinary1024 ? "" : "i");
        return String.Format("{0:F1} {1}B", bytes / Math.Pow(unit, exp), pre);
    }
}