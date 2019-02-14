public class foo{
	private BufferedImage bufferedImageFromBitmap( final HDC blitDC, final HBITMAP outputBitmap ) {
		final BITMAPINFOHEADER bih   = bi.bmiHeader;
		
		final int height             = Math.abs( bih.biHeight );
		final int strideBits         = ( bih.biWidth * bih.biBitCount );
		final int strideBytesAligned = ( ( ( strideBits - 1 ) | 0x1F ) + 1 ) >> 3;
		final int strideElementsAligned;
		
		switch ( bih.biBitCount ) {
		case 16:
			strideElementsAligned = strideBytesAligned / 2;
			if ( buffer == null ) {
				cm     = new DirectColorModel( 16, 0x7C00, 0x3E0, 0x1F );
    			buffer = new DataBufferUShort( strideElementsAligned * height );
    			raster = Raster.createPackedRaster( buffer, bih.biWidth, height, strideElementsAligned, ( (DirectColorModel) cm ).getMasks(), null );
			}
			break;
		case 32:
			strideElementsAligned = strideBytesAligned / 4;
			if ( buffer == null ) {
				cm     = new DirectColorModel( 32, 0xFF0000, 0xFF00, 0xFF );
    			buffer = new DataBufferInt( strideElementsAligned * height );
    			raster = Raster.createPackedRaster( buffer, bih.biWidth, height, strideElementsAligned, ( (DirectColorModel) cm ).getMasks(), null );
			}
			break;
		default:
			throw new IllegalArgumentException( "Unsupported bit count: " + bih.biBitCount );
		}
		
		final boolean ok;
		switch ( buffer.getDataType() ) {
		case DataBuffer.TYPE_INT : {
			final int[] pixels = ( (DataBufferInt) buffer ).getData();
			ok = GDI.GetDIBits( blitDC, outputBitmap, 0, raster.getHeight(), pixels, bi, 0 );
			break;
		}
		case DataBuffer.TYPE_USHORT : {
			final short[] pixels = ( (DataBufferUShort) buffer ).getData();
			ok = GDI.GetDIBits( blitDC, outputBitmap, 0, raster.getHeight(), pixels, bi, 0 );
			break;
		}
		default:
			throw new AssertionError( "Unexpected buffer element type: " + buffer.getDataType() );
		}
		
		if ( ok ) {
			if ( resultBufferedImage == null )
				resultBufferedImage = new BufferedImage( cm, raster, false, null );
			return resultBufferedImage;
		}
		else
			return null;
	}
}