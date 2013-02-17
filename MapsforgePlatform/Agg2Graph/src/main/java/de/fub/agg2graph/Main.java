/*******************************************************************************
 * Copyright (c) 2012 Johannes Mitlmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Affero Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/agpl-3.0.html
 * 
 * Contributors:
 *     Johannes Mitlmeier - initial API and implementation
 ******************************************************************************/
package de.fub.agg2graph;

import de.fub.agg2graph.ui.cli.AutoTest;
import de.fub.agg2graph.ui.cli.CLI;
import de.fub.agg2graph.ui.gui.DrawGPX;
import de.fub.agg2graph.ui.gui.TestUI;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
	private static final String VERSION = "0.1";

	/**
	 * This is the Main class supposed to be exposed to the user when making a
	 * JAR file. It handles the selection of the different tools available via a
	 * command line parameter.
	 * 
	 * @param args
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException,
			IOException, InterruptedException {
		// setup logging on root logger to use inheritance
		Logger l = Logger.getLogger("");
		l.setUseParentHandlers(false);
		LogFormatter formatter = new LogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		// output log level
		handler.setLevel(Level.INFO);
		for (Handler h : l.getHandlers()) {
			l.removeHandler(h);
		}
		l.addHandler(handler);

		Logger logger = Logger.getLogger("agg2graph.main");
		logger.severe(MessageFormat.format("agg2graph {0}", VERSION));

		// parse parameters
		if (args.length < 1) {
			args = new String[] { "testui" };
		}
		String type = args[0].toLowerCase();
		if (type.equals("drawgpx")) {
			DrawGPX.main(Arrays.copyOfRange(args, 1, args.length));
		} else if (type.equals("cli") || type.equals("c")) {
			CLI.main(Arrays.copyOfRange(args, 1, args.length));
		} else if (type.equals("testui") || type.equals("t")) {
			TestUI.main(Arrays.copyOfRange(args, 1, args.length));
		} else if (type.equals("autotest") || type.equals("a")) {
			AutoTest.main(Arrays.copyOfRange(args, 1, args.length));
		} else if (type.equals("parameters") || type.equals("p")) {
			CLI.printParameters();
		} else {
			System.out
					.println("No type parameter given. must be one of cli, testui, drawgpx, autotest, parameters.");
		}
	}
}
