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

public class Token {
	public String name;
	public String value;

	public Token(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public boolean isKeyValue() {
		return value != null;
	}

	@Override
	public String toString() {
		return name + (value == null ? "" : "=" + value);
	}
}
