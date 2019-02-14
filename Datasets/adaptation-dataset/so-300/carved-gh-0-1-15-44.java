public class foo{
	/**
	 * The following must convert double to String in a much more efficient way then Double.toString()
	 *
	 * @See http://stackoverflow.com/questions/10553710/fast-double-to-string-conversion-with-given-precision
	 * @param val
	 * @param precision
	 * @return
	 */
	protected static String fastDoubleToString(double val, int precision) {
		StringBuilder sb = new StringBuilder();
		if (val < 0) {
			sb.append('-');
			val = -val;
		}
		long exp = POW10[precision];
		long lval = (long)(val * exp + 0.5);
		sb.append(lval / exp).append('.');
		long fval = lval % exp;
		for (int p = precision - 1; p > 0 && fval < POW10[p] && fval>0; p--) {
			sb.append('0');
		}
		sb.append(fval);
		int i = sb.length()-1;
		while(sb.charAt(i)=='0' && sb.charAt(i-1)!='.')
		{
			sb.deleteCharAt(i);
			i--;
		}
		return sb.toString();
	}
}