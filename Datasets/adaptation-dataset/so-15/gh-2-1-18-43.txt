package kianxali.util;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * This class implements a log formatter that simply prefixes log entries with
 * their severity.
 *
 * Based on <a href='http://stackoverflow.com/questions/2950704/java-util-logging-how-to-suppress-date-line'>
 * http://stackoverflow.com/questions/2950704/java-util-logging-how-to-suppress-date-line</a>
 *
 * @author fwi
 *
 */
public class LogFormatter extends Formatter {
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
