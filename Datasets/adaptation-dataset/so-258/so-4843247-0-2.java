public class foo {
    public static BufferedImage getScreenshot(Rectangle bounds) {
        W32API.HDC windowDC = GDI.GetDC(USER.GetDesktopWindow());
        W32API.HBITMAP outputBitmap =
            GDI.CreateCompatibleBitmap(windowDC,
                                       bounds.width, bounds.height);
        try {
            W32API.HDC blitDC = GDI.CreateCompatibleDC(windowDC);
            try {
                W32API.HANDLE oldBitmap =
                    GDI.SelectObject(blitDC, outputBitmap);
                try {
                    GDI.BitBlt(blitDC,
                               0, 0, bounds.width, bounds.height,
                               windowDC,
                               bounds.x, bounds.y,
                               GDI32.SRCCOPY);
                } finally {
                    GDI.SelectObject(blitDC, oldBitmap);
                }
                GDI32.BITMAPINFO bi = new GDI32.BITMAPINFO(40);
                bi.bmiHeader.biSize = 40;
                boolean ok =
                    GDI.GetDIBits(blitDC, outputBitmap, 0, bounds.height,
                                  (byte[]) null, bi, GDI32.DIB_RGB_COLORS);
                if (ok) {
                    GDI32.BITMAPINFOHEADER bih = bi.bmiHeader;
                    bih.biHeight = - Math.abs(bih.biHeight);
                    bi.bmiHeader.biCompression = 0;
                    return bufferedImageFromBitmap(blitDC, outputBitmap, bi);
                } else {
                    return null;
                }
            } finally {
                GDI.DeleteObject(blitDC);
            }
        } finally {
            GDI.DeleteObject(outputBitmap);
        }
    }
}