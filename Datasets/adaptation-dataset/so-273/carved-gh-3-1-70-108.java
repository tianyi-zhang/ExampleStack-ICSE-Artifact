public class foo{
  /**
   * <a href=
   * "http://stackoverflow.com/questions/7896280/converting-from-hsv-hsb-in-java-to-rgb-without-using-java-awt-color-disallowe">
   * Converting from HSV (HSB in Java) to RGB without using
   * java.awt.Color</a>
   */
  public static Color fromHSV(final float hue,
                              final float saturation,
                              final float value)
  {
    final float normaliedHue = hue - (float) Math.floor(hue);
    final int h = (int) (normaliedHue * 6);
    final float f = normaliedHue * 6 - h;
    final float p = value * (1 - saturation);
    final float q = value * (1 - f * saturation);
    final float t = value * (1 - (1 - f) * saturation);

    switch (h)
    {
      case 0:
        return fromRGB(value, t, p);
      case 1:
        return fromRGB(q, value, p);
      case 2:
        return fromRGB(p, value, t);
      case 3:
        return fromRGB(p, q, value);
      case 4:
        return fromRGB(t, p, value);
      case 5:
        return fromRGB(value, p, q);
      default:
        throw new RuntimeException(String
          .format("Could not convert from HSV (%f, %f, %f) to RGB",
                  normaliedHue,
                  saturation,
                  value));
    }
  }
}