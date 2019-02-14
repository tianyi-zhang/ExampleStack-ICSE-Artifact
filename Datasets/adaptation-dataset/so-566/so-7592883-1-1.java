public class foo {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest hsr = (HttpServletRequest) request;
        String path = hsr.getRequestURI().substring(hsr.getContextPath().length());
        GodFilterChain godChain = new GodFilterChain(chain);

        for (Entry<Pattern, Filter> entry : filters.entrySet()) {
            if (entry.getKey().matches(path)) {
                godChain.addFilter(entry.getValue());
            }
        }

        godChain.doFilter(request, response);
    }
}