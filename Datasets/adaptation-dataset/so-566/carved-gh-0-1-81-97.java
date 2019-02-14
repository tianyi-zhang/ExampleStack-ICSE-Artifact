public class foo{
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
    HttpServletRequest hsr = (HttpServletRequest) request;
    if (filters.length == 0) {
      chain.doFilter(hsr, response);
    } else {
      String path = hsr.getRequestURI().replaceFirst(hsr.getContextPath(), "");
      GodFilterChain godChain = new GodFilterChain(chain);

      for (ServletFilter filter : filters) {
        if (filter.doGetPattern().matches(path)) {
          godChain.addFilter(filter);
        }
      }
      godChain.doFilter(hsr, response);
    }
  }
}