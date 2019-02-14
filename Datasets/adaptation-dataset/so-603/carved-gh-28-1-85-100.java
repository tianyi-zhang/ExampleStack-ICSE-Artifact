public class foo{
    private static Integer versionCompare(String str1, String str2) {
	// code taken from http://stackoverflow.com/a/6702029
	String[] vals1 = str1.split("\\.");
	String[] vals2 = str2.split("\\.");
	int i = 0;

	while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
	    i++;
	}

	if (i < vals1.length && i < vals2.length) {
	    return Integer.signum(Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i])));
	}

	return Integer.signum(vals1.length - vals2.length);
    }
}