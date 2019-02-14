public class foo {
    public FRexPHolder frexp (double value) {
      FRexPHolder ret = new FRexPHolder();

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