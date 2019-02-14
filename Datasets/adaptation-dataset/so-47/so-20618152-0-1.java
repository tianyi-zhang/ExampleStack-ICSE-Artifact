public class foo {
/**
 * Draw an image to the specified coordinates onto a single page. <br>
 * Also scaled the image with the specified factor.
 * 
 * @author Nick Russler
 * @param document PDF document the image should be written to.
 * @param pdfpage Page number of the page in which the image should be written to.
 * @param x X coordinate on the page where the left bottom corner of the image should be located. Regard that 0 is the left bottom of the pdf page.
 * @param y Y coordinate on the page where the left bottom corner of the image should be located.
 * @param scale Factor used to resize the image.
 * @param imageFilePath Filepath of the image that is written to the PDF.
 * @throws IOException
 */
public static void addImageToPage(PDDocument document, int pdfpage, int x, int y, float scale, String imageFilePath) throws IOException {   
    // Convert the image to TYPE_4BYTE_ABGR so PDFBox won't throw exceptions (e.g. for transparent png's).
    BufferedImage tmp_image = ImageIO.read(new File(imageFilePath));
    BufferedImage image = new BufferedImage(tmp_image.getWidth(), tmp_image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);        
    image.createGraphics().drawRenderedImage(tmp_image, null);

    PDXObjectImage ximage = new PDPixelMap(document, image);

    PDPage page = (PDPage)document.getDocumentCatalog().getAllPages().get(pdfpage);

    PDPageContentStream contentStream = new PDPageContentStream(document, page, true, true);
    contentStream.drawXObject(ximage, x, y, ximage.getWidth()*scale, ximage.getHeight()*scale);
    contentStream.close();
}
}