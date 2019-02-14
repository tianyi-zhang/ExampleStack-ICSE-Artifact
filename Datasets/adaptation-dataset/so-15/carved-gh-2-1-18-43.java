public class foo{
    @Override
    public String format(final LogRecord r) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%7s: ", r.getLevel()));
        sb.append(formatMessage(r)).append(System.getProperty("line.separator"));
        if(null != r.getThrown()) {
            sb.append("Throwable occurred: ");
            Throwable t = r.getThrown();
            PrintWriter pw = null;
            try {
                StringWriter sw = new StringWriter();
                pw = new PrintWriter(sw);
                t.printStackTrace(pw);
                sb.append(sw.toString());
            } finally {
                if(pw != null) {
                    try {
                        pw.close();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return sb.toString();
    }
}