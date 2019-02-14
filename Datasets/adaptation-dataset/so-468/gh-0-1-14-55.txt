package se.springworks.android.utils.stream;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class StreamUtils {

	/**
	 * Compares two streams
	 * http://stackoverflow.com/a/4245962/1266551
	 * @param i1
	 * @param i2
	 * @return
	 */
	public static boolean isEqual(InputStream i1, InputStream i2) {
	    ReadableByteChannel ch1 = Channels.newChannel(i1);
	    ReadableByteChannel ch2 = Channels.newChannel(i2);

	    ByteBuffer buf1 = ByteBuffer.allocateDirect(1024);
	    ByteBuffer buf2 = ByteBuffer.allocateDirect(1024);

	    try {
	        while (true) {

	            int n1 = ch1.read(buf1);
	            int n2 = ch2.read(buf2);

	            if (n1 == -1 || n2 == -1) return n1 == n2;

	            buf1.flip();
	            buf2.flip();

	            for (int i = 0; i < Math.min(n1, n2); i++)
	                if (buf1.get() != buf2.get())
	                    return false;

	            buf1.compact();
	            buf2.compact();
	        }

	    }
	    catch(IOException e) {
	    	return false;
	    }
	    finally {
	    	StreamUtils.closeSilently(i1);
	    	StreamUtils.closeSilently(i2);
	    }
	}
	
	/**
	 * Get a string with default encoding from a stream
	 * From: http://stackoverflow.com/a/5445161/1266551
	 * @param is
	 * @return
	 */
	public static String getAsString(InputStream is) {
		if(is == null) {
			return null;
		}
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : null;
	}
	
	/**
	 * Get a string with a specific encoding from a stream
	 * http://stackoverflow.com/a/5445161/1266551
	 * @param is
	 * @param encoding
	 * @return
	 */
	public static String getAsString(InputStream is, String encoding) {
		if(is == null) {
			return null;
		}
		java.util.Scanner s = new java.util.Scanner(is, encoding).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : null;
	}
	
	public static byte[] getAsBytes(InputStream is, int bufferSize)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(bufferSize);
		int oneByte;
		while ((oneByte = is.read()) != -1) {
			baos.write(oneByte);
		}
		return baos.toByteArray();
	}

	public static byte[] getAsBytes(InputStream is) throws IOException {
		return getAsBytes(is, 32);
	}

	/**
	 * http://stackoverflow.com/questions/2787015/skia-decoder-fails-to-decode-remote-stream
	 * 
	 * @author bjorn.ritzl
	 */
	public static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = this.in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int byteValue = read();
					if (byteValue < 0) {
						break; // we reached EOF
					}
					bytesSkipped = 1; // we read one byte
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}
	
	/**
	 * Closes a stream or similar class implementing {@link Closeable} without
	 * throwing any exceptions
	 * @param closable The object to close. Can be null
	 */
	public static void closeSilently(Closeable closable) {
		if(closable == null) {
			return;
		}
		try {
			closable.close();
		} catch (IOException e) {
			// do nothing
		}
	}
}
