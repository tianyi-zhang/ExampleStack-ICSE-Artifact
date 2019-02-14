public class foo{
    @Override
    public void handleResourceRequest(FacesContext context) throws IOException {
        ExternalContext externalContext = context.getExternalContext();
        String resourceName = externalContext.getRequestPathInfo();
        String libraryName = externalContext.getRequestParameterMap().get("ln");
        Resource resource = context.getApplication().getResourceHandler().createResource(resourceName, libraryName);

        if (resource == null) {
            super.handleResourceRequest(context);
            return;
        }

        if (!resource.userAgentNeedsUpdate(context)) {
            externalContext.setResponseStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        externalContext.setResponseContentType(resource.getContentType());

        for (Entry<String, String> header : resource.getResponseHeaders().entrySet()) {
            externalContext.setResponseHeader(header.getKey(), header.getValue());
        }

        ReadableByteChannel input = null;
        WritableByteChannel output = null;

        try {
            input = Channels.newChannel(resource.getInputStream());
            output = Channels.newChannel(externalContext.getResponseOutputStream());

            for (ByteBuffer buffer = ByteBuffer.allocateDirect(10240); input.read(buffer) != -1; buffer.clear()) {
                output.write((ByteBuffer) buffer.flip());
            }
        }
        finally {
            if (output != null) try { output.close(); } catch (IOException ignore) {}
            if (input != null) try { input.close(); } catch (IOException ignore) {}
        }
    }
}