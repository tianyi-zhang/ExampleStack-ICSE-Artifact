public class foo{
	    /**
	     * Flushes this output stream and forces any buffered output bytes to be
	     * written out. The general contract of <code>flush</code> is that
	     * calling it is an indication that, if any bytes previously written
	     * have been buffered by the implementation of the output stream, such
	     * bytes should immediately be written to their intended destination.
	     */
	    @Override
	    public void flush() {

	        if (count == 0) {
	            return;
	        }

	        // don't print out blank lines; flushing from PrintStream puts out
	        // these
	        if (count == LINE_SEPERATOR.length()) {
	            if (((char) buf[0]) == LINE_SEPERATOR.charAt(0) && ((count == 1) || // <-
	                                                                                // Unix
	                                                                                // &
	                                                                                // Mac,
	                                                                                // ->
	                                                                                // Windows
	                    ((count == 2) && ((char) buf[1]) == LINE_SEPERATOR.charAt(1)))) {
	                reset();
	                return;
	            }
	        }

	        final byte[] theBytes = new byte[count];

	        System.arraycopy(buf, 0, theBytes, 0, count);

	        if (isError) {
	            log.error(new String(theBytes));
	        } else {
	            log.info(new String(theBytes));
	        }

	        reset();
	    }
}