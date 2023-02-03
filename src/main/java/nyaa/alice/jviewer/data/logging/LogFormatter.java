package nyaa.alice.jviewer.data.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter
{
    private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

    @Override
    public synchronized String format(LogRecord lr)
    {
        if (lr.getThrown() != null)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            lr.getThrown().printStackTrace(pw);

            // i coudln't make it format the stack trace for some reason so just doing that
            // cause yeah
            return String.format(format, new Date(lr.getMillis()), lr.getLevel().getLocalizedName(), lr.getMessage())
                    + sw.toString();
        }
        return String.format(format, new Date(lr.getMillis()), lr.getLevel().getLocalizedName(), lr.getMessage());
    }
}
