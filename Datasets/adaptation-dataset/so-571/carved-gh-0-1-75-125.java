public class foo{
	/**
	 * Implementation of the C <code>frexp</code> function to break
	 * floating-point number into normalized fraction and power of 2.
	 *
	 * @see "http://stackoverflow.com/questions/1552738/is-there-a-java-equivalent-of-frexp"
	 *
	 * @param value
	 *            the value
	 * @return the exponent and mantissa of the input value
	 */
	public static ExponentAndMantissa frexp(double value) {
		final ExponentAndMantissa ret = new ExponentAndMantissa();

		ret.exponent = 0;
		ret.mantissa = 0;

		if (value == 0.0 || value == -0.0) {
			return ret;
		}

		if (Double.isNaN(value)) {
			ret.mantissa = Double.NaN;
			ret.exponent = -1;
			return ret;
		}

		if (Double.isInfinite(value)) {
			ret.mantissa = value;
			ret.exponent = -1;
			return ret;
		}

		ret.mantissa = value;
		ret.exponent = 0;
		int sign = 1;

		if (ret.mantissa < 0f) {
			sign--;
			ret.mantissa = -(ret.mantissa);
		}
		while (ret.mantissa < 0.5f) {
			ret.mantissa *= 2.0f;
			ret.exponent -= 1;
		}
		while (ret.mantissa >= 1.0f) {
			ret.mantissa *= 0.5f;
			ret.exponent++;
		}
		ret.mantissa *= sign;
		return ret;
	}
}