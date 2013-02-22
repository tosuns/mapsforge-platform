/*******************************************************************************
   Copyright 2013 Johannes Mitlmeier

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
******************************************************************************/
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
