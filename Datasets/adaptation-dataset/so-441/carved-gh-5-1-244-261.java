public class foo{
  private static EnumOS getOs() {
    String s = System.getProperty("os.name").toLowerCase();
    if (s.contains("win")) {
      return EnumOS.windows;
    } else if (s.contains("mac")) {
      return EnumOS.macos;
    } else if (s.contains("solaris")) {
      return EnumOS.solaris;
    } else if (s.contains("sunos")) {
      return EnumOS.solaris;
    } else if (s.contains("linux")) {
      return EnumOS.linux;
    } else if (s.contains("unix")) {
      return EnumOS.linux;
    } else {
      return EnumOS.unknown;
    }
  }
}