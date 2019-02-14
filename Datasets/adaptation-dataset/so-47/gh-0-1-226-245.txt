package org.foo;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.ExtensionsData;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.CmisService;
import org.apache.chemistry.opencmis.server.shared.ThresholdOutputStream;
import org.apache.chemistry.opencmis.server.shared.ThresholdOutputStreamFactory;
import org.apache.chemistry.opencmis.server.support.wrapper.AbstractCmisServiceWrapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example of a minimal CMIS Custom Service Wrapper (logging example)
 * 
 * Add the following ** to the repo.properties to have framework hook into chain
 * The number at the key is the position in the wrapper stack. Lower numbers are
 * outer wrappers, higher numbers are inner wrappers.
 *
 * ** add the following line to your repo.properties file in your servers war
 * 
 * <pre>
 * servicewrapper.1=org.apache.chemistry.opencmis.server.support.CmisCustomLoggingServiceWrapper
 * </pre>
 */
public class CmisCustomPdfWatermarkServiceWrapper extends AbstractCmisServiceWrapper {

    // slf4j example
    private static final Logger LOG = LoggerFactory.getLogger(CmisCustomPdfWatermarkServiceWrapper.class);

    // constants for this extension
    public static final String USER_FILTER_NAME = "userfilter";

    private String userToWatermark = null;

    // provide constructor
    public CmisCustomPdfWatermarkServiceWrapper(CmisService service) {
        super(service);
    }

    /**
     * Initializes the wrapper with a set of optional parameters from the
     * properties file
     */
    @Override
    public void initialize(Object[] params) {

        // Whenever CmisServiceWrapperManager.wrap() is called, a new wrapper
        // instance is created and the corresponding initialize() method is
        // called.
        // If the service factory creates a new CmisService object and wraps it
        // with every request, then new wrapper instances are created and
        // initialized for every request.
        // This is why in this case you will see initialize get called for every
        // request.
        // Depending on your implementation you may only see this called once.

        LOG.info("Initializing the CmisCustomPdfWatermarkServiceWrapper.");

        for (Object parm : params) {
            if (parm == null) {
                continue;
            }

            LOG.info("[CmisCustomPdfWatermarkServiceWrapper]Parameter:" + parm.toString());

            // Let's store the ID of the user that we want to flag for
            // watermarking.
            String[] parts = parm.toString().split("=");

            if (USER_FILTER_NAME.equalsIgnoreCase(parts[0])) {
                // we have a user
                userToWatermark = parts[1];
            }
        }
    }

    /**
     * slf logging version with dual output to console and slf
     */
    protected void slflog(String operation, String repositoryId) {
        if (repositoryId == null) {
            repositoryId = "<none>";
        }

        HttpServletRequest request = (HttpServletRequest) getCallContext().get(CallContext.HTTP_SERVLET_REQUEST);
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            userAgent = "<unknown>";
        }

        String binding = getCallContext().getBinding();

        LOG.info("Operation: {}, Repository ID: {}, Binding: {}, User Agent: {}", operation, repositoryId, binding,
                userAgent);
    }

    @Override
    public ContentStream getContentStream(String repositoryId, String objectId, String streamId, BigInteger offset,
            BigInteger length, ExtensionsData extension) {

        slflog("getContentStream override from Chameleon module --------------", repositoryId);
        long startTime = System.currentTimeMillis();

        CallContext sharedContext = this.getCallContext();

        // Get the native domain object from the call context if one is shared
        // by the vendor (example only)
        // Your CMIS vendor's documentation must expose the name of any shared
        // objects they place here for extensions.
        // Object objShared = sharedContext.get("shared_key_name_from_vendor");

        ContentStream retVal = getWrappedService().getContentStream(repositoryId, objectId, streamId, offset, length,
                extension);

        if (sharedContext.getUsername().equalsIgnoreCase(userToWatermark)) {
            if ((retVal != null) && (retVal.getMimeType().contains("pdf"))) {
                InputStream rawStream = retVal.getStream();

                // return a pdfbox document object
                // for debugging only - load to pdfbox and stream out
                // PDDocument modifiedPDF = watermarkPDF_loadOnly(rawStream);
                // actual watermark code
                PDDocument modifiedPDF = watermarkPDF(rawStream);

                // Extra credit here. Replace with ThresholdOutputStream or find
                // another way to handle very large objects in a small memory
                // footprint.
                // ByteArrayOutputStream out = new ByteArrayOutputStream();
                ThresholdOutputStream out;
                ThresholdOutputStreamFactory outFactory = (ThresholdOutputStreamFactory) sharedContext
                        .get(CallContext.STREAM_FACTORY);
                if (outFactory != null) {
                    // reuse the server factory configuration
                    out = outFactory.newOutputStream();
                } else {
                    // there is no default ThresholdOutputStreamFactory
                    // -> create a stream manually:
                    // default temp directory, max 4MiB in main memory,
                    // unlimited content size
                    out = new ThresholdOutputStream(null, 4 * 1024 * 1024, -1);
                }

                try {
                    modifiedPDF.save(out);
                    modifiedPDF.close();
                    InputStream modifiedInputStream = out.getInputStream(); // new
                                                                            // ByteArrayInputStream(out.toByteArray());

                    // Extra credit here. Handle offset and length if provided
                    // by the client.

                    // now write the stream back to the ContentStream object
                    retVal = new ContentStreamImpl(retVal.getFileName(), null, "application/pdf", modifiedInputStream);

                } catch (Exception e) {
                    slflog("error transposing stream getContentStream ", e.getMessage());

                    LOG.error("Could not watermark PDF document: {}", e.getMessage(), e);
                    throw new CmisRuntimeException("Could not watermark PDF document!");
                }
            } // if pdf stream
        } // if user matches filter param

        LOG.info("[CmisCustomServiceWrapper] Exiting method getContentStream. time (ms):"
                + (System.currentTimeMillis() - startTime));
        // System.out.println("[CmisCustomServiceWrapper] Exiting method getContentStream. time (ms):"
        // + (System.currentTimeMillis() - startTime));

        return retVal;
    }

    public PDDocument watermarkPDF(InputStream rawStream) {
        PDDocument pdf = null;
        try {

            pdf = PDDocument.load(rawStream);

            // Load watermark from classes directory cmis.jpg
            // Alternatives:
            // 1. Use a pdf document from the repository (via a fixed path) so
            // the watermark can be changed at runtime by an administrator.
            // 2. Dynamically generate a watermark based on the username / time
            // date and users ip address.
            InputStream watermarkStream = CmisCustomPdfWatermarkServiceWrapper.class.getClassLoader()
                    .getResourceAsStream("cmis.png");
            BufferedImage buffered = ImageIO.read(watermarkStream);

            // Exercise A - add image to all pages of document.
            // Loop through pages in PDF
            // List pages = pdf.getDocumentCatalog().getAllPages();
            // System.out.println("pdf loaded from stream. Pages found:" +
            // pages.size());
            // Iterator iter = pages.iterator();
            // while(iter.hasNext())
            // {
            // PDPage page = (PDPage)iter.next();
            // ...

            addImageToPage(pdf, 0, 0, 0, 1.0f, buffered);

            // example of how to set metadata on the document
            pdf.getDocumentInformation().setTitle("Modified by fileBridge");
        } catch (Exception e) {
            slflog("error watermarking pdf in getContentStream ", e.getMessage());
        }

        return pdf;
    }

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
