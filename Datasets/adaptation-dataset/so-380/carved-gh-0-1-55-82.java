public class foo{
	/**
	 * get the a CSV File from the given urlString
	 * http://stackoverflow.com/questions/27224870/getting-http-400-error-when-trying-to-download-file-using-jersey-client
	 * 
	 * @param urlString
	 * @param csvFile
	 * @throws IOException
	 */
	public void getCSVAsFile(String urlString, File csvFile) throws IOException {
		client = ApacheHttpClient.create();
		WebResource webResource = client.resource(urlString);
		WebResource.Builder wb = webResource
				.accept("application/json,application/pdf,text/plain,image/jpeg,application/xml,application/vnd.ms-excel");
		ClientResponse response = wb.get(ClientResponse.class);

		if (response.getStatus() != 200) {
			throw new RuntimeException("HTTP error code : " + response.getStatus());
		}

		InputStream input = response.getEntity(InputStream.class);

		byte[] byteArray = org.apache.commons.io.IOUtils.toByteArray(input);

		FileOutputStream fos = new FileOutputStream(csvFile);
		fos.write(byteArray);
		fos.flush();
		fos.close();
	}
}