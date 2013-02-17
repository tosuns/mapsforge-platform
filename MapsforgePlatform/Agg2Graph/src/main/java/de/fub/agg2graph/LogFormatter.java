package de.fub.agg2graph;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Simple {@link Formatter} for log messages.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class LogFormatter extends Formatter {
	private final DateFormat df = new SimpleDateFormat(
			"dd.MM.yyyy hh:mm:ss.SSS");
	private final boolean simple = true;

	@Override
	public String format(LogRecord record) {
		if (simple) {
			return String.format("%s\n", formatMessage(record));
		} else {
			return String.format("%s - %s: %s (%s)\n",
					df.format(new Date(record.getMillis())), record.getLevel(),
					formatMessage(record), record.getSourceMethodName());
		}
	}
}
