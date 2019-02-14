public class foo{
    // Original code taken from Nick Russler's example here:
    // https://stackoverflow.com/questions/8929954/watermarking-with-pdfbox
    // and modified to use a BufferedImage directly.
    // Thank's Nick.
    //
    public void addImageToPage(PDDocument document, int pdfpage, int x, int y, float scale, BufferedImage tmp_image)
            throws IOException {
        // Convert the image to TYPE_4BYTE_ABGR so PDFBox won't throw exceptions
        // (e.g. for transparent png's).
        BufferedImage image = new BufferedImage(tmp_image.getWidth(), tmp_image.getHeight(),
                BufferedImage.TYPE_4BYTE_ABGR);
        image.createGraphics().drawRenderedImage(tmp_image, null);

        PDXObjectImage ximage = new PDPixelMap(document, image);
        PDPage page = (PDPage) document.getDocumentCatalog().getAllPages().get(pdfpage);
        PDPageContentStream contentStream = new PDPageContentStream(document, page, true, true);

        contentStream.drawXObject(ximage, x, y, ximage.getWidth() * scale, ximage.getHeight() * scale);
        contentStream.close();
    }
}