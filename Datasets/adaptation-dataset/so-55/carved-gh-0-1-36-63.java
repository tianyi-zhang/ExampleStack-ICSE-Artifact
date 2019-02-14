public class foo{
	public static void main(String[] args) throws Exception {
		SubmitReq sr = new SubmitReq();
		sr.setNamespace("http://www.3gpp.org/ftp/Specs/archive/23_series/23.140/schema/REL-5-MM7-1-2");
		sr.setMm7Version("5.3.0");
		sr.setVaspId("Ejada");
		sr.setVasId("Ejada");
		sr.setSubject("Allah Akbar");
		sr.setDeliveryReport(true);
		sr.addRecipient(new Address("1111", RecipientType.TO));
		sr.setTransactionId("1348056868070-1-");

		// Attach a SMIL presentation
		BinaryContent smil = new BinaryContent("application/smil", load("smil-sample2.xml"));
		smil.setContentId("smil");

		// Attach a picture
		BinaryContent image = new BinaryContent("image/jpeg", load("lorena.jpg"));
		image.setContentId("image");

		// Attach a text
		TextContent text = new TextContent("Lorena sends a lots of hugs!");
		text.setContentId("text");

		// Pack it all up
		sr.setContent(new BasicContent(smil, image, text));

		MM7Message.save(sr, System.out, new MM7Context());
	}
}