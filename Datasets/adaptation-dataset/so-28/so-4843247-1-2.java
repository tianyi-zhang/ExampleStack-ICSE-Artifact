public class foo {
    private static BufferedImage
    bufferedImageFromBitmap(GDI32.HDC        blitDC,
                            GDI32.HBITMAP    outputBitmap,
                            GDI32.BITMAPINFO bi) {
        GDI32.BITMAPINFOHEADER bih = bi.bmiHeader;
        int height = Math.abs(bih.biHeight);
        final ColorModel cm;
        final DataBuffer buffer;
        final WritableRaster raster;
        int strideBits =
            (bih.biWidth * bih.biBitCount);
        int strideBytesAligned =
            (((strideBits - 1) | 0x1F) + 1) >> 3;
        final int strideElementsAligned;
        switch (bih.biBitCount) {
        case 16:
            strideElementsAligned = strideBytesAligned / 2;
            cm = new DirectColorModel(16, 0x7C00, 0x3E0, 0x1F);
            buffer =
                new DataBufferUShort(strideElementsAligned * height);
            raster =
                Raster.createPackedRaster(buffer,
                                          bih.biWidth, height,
                                          strideElementsAligned,
                                          ((DirectColorModel) cm).getMasks(),
                                          null);
            break;
        case 32:
            strideElementsAligned = strideBytesAligned / 4;
            cm = new DirectColorModel(32, 0xFF0000, 0xFF00, 0xFF);
            buffer =
                new DataBufferInt(strideElementsAligned * height);
            raster =
                Raster.createPackedRaster(buffer,
                                          bih.biWidth, height,
                                          strideElementsAligned,
                                          ((DirectColorModel) cm).getMasks(),
                                          null);
            break;
        default:
            throw new IllegalArgumentException("Unsupported bit count: " + bih.biBitCount);
        }
        final boolean ok;
        switch (buffer.getDataType()) {
            case DataBuffer.TYPE_INT:
                {
                    int[] pixels = ((DataBufferInt) buffer).getData();
                    ok = GDI.GetDIBits(blitDC, outputBitmap, 0, raster.getHeight(), pixels, bi, 0);
                }
                break;
            case DataBuffer.TYPE_USHORT:
                {
                    short[] pixels = ((DataBufferUShort) buffer).getData();
                    ok = GDI.GetDIBits(blitDC, outputBitmap, 0, raster.getHeight(), pixels, bi, 0);
                }
                break;
            default:
                throw new AssertionError("Unexpected buffer element type: " + buffer.getDataType());
        }
        if (ok) {
            return new BufferedImage(cm, raster, false, null);
        } else {
            return null;
        }
    }
}