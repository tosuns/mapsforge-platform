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
package de.fub.agg2graph.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DSL {

	// prepare regexp for splitting the lines
	public final static Pattern pattern = Pattern
			.compile("([^=,\\s][^=\\s,]*)(\\s*=\\s*(([^\"\\s,]+)|(\"(.*?)\")))?");

	public static List<Token> getTokens(String line) {
		ArrayList<Token> tokens = new ArrayList<Token>(5);
		// comments
		if (line.startsWith("#") || line.startsWith("//")) {
			return tokens;
		}
		// extract tokens
		Matcher matcher = pattern.matcher(line);
		while (matcher.find()) {
			Token token = new Token(matcher.group(1),
					matcher.group(6) != null ? matcher.group(6)
							: matcher.group(3));
			// System.out.println("> " + token);
			tokens.add(token);
		}
		return tokens;
	}

	public static Map<String, String> getKeyValueMap(List<Token> tokens) {
		Map<String, String> keyValueMap = new HashMap<String, String>();
		for (Token token : tokens) {
			if (token.value != null) {
				keyValueMap.put(token.name, token.value);
			}
		}
		return keyValueMap;
	}

}
