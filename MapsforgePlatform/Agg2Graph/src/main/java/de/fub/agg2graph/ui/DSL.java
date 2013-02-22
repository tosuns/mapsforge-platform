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
