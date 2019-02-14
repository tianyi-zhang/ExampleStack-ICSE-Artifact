public class foo {
public static BigInteger[] convertToFraction(double value) {
  int exponent = Math.getExponent(value);
  if (exponent > Double.MAX_EXPONENT) {
    // The value is infinite or NaN.
    throw new IllegalArgumentException("Illegal parameter 'value': " + value);
  }
  long positiveSignificand;
  if (exponent < Double.MIN_EXPONENT) {
    // The value is subnormal.
    exponent++;
    positiveSignificand = Double.doubleToLongBits(value) & 0x000fffffffffffffL;
  } else {
    positiveSignificand = (Double.doubleToLongBits(value) & 0x000fffffffffffffL) | 0x0010000000000000L;
  }
  BigInteger significand = BigInteger.valueOf(value < 0 ? -positiveSignificand : positiveSignificand);
  exponent -= 52; // Adjust the exponent for an integral significand.
  BigInteger coefficient = BigInteger.ONE.shiftLeft(Math.abs(exponent));
  if (exponent >= 0) {
    return new BigInteger[] { significand.multiply(coefficient), BigInteger.ONE };
  } else {
    BigInteger gcd = significand.gcd(coefficient);
    return new BigInteger[] { significand.divide(gcd), coefficient.divide(gcd) };
  }
}
}