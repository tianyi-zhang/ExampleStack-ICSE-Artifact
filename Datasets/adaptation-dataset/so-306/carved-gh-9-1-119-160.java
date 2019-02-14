public class foo{
	/**
	 * Get data attached in http request
	 * @param request
	 * @return Data attached in Http request body 
	 * @throws IOException
	 */
	public static String getRequestData(HttpServletRequest request) throws IOException {
		
		// This code is copy pasted from http://stackoverflow.com/questions/14525982/getting-request-payload-from-post-request-in-java-servlet
	    String body = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    BufferedReader bufferedReader = null;
	    int bufferSize = 128;
	    try {
	        ServletInputStream inputStream = request.getInputStream();
	        if (inputStream != null) {
	        	InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	            bufferedReader = new BufferedReader(inputStreamReader);
	            char[] charBuffer = new char[bufferSize];
	            int bytesRead = -1;
	            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
	                stringBuilder.append(charBuffer, 0, bytesRead);
	            }
	        } else {
	            stringBuilder.append("");
	        }
	    } catch (IOException ex) {
	        logger.log(Level.INFO, ex);
	    } finally {
	        if (bufferedReader != null) {
	            try {
	                bufferedReader.close();
	            } catch (IOException ex) {
	    	        logger.log(Level.INFO, ex);
	            }
	        }
	    }

	    body = stringBuilder.toString();
	    return body;
	    
	}
}