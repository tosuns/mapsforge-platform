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
package de.fub.agg2graph.structs;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Parameter that is changeable, either with getter/setter logic or because it
 * is a public attribute. Think JavaBean.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class EditableObject {
	public String name;
	public Method getter;
	public Method setter;
	public Object value;
	public Field field;

	private EditableObject() {
	}

	public static EditableObject makeFromMethod(String name, Object value,
			Method getter, Method setter) {
		EditableObject eo = new EditableObject();
		eo.name = name;
		eo.parseValue(value);
		eo.value = value;
		eo.setter = setter;
		eo.getter = getter;
		return eo;
	}

	public static EditableObject makeFromField(String name, Object value,
			Field field) {
		EditableObject eo = new EditableObject();
		eo.name = name;
		eo.parseValue(value);
		eo.field = field;
		return eo;
	}

	public void parseValue(Object value) {
		this.value = value;
	}

	public boolean isField() {
		return getter == null || setter == null;
	}

	public boolean isMethod() {
		return !isField();
	}

	public String getTypeString() {
		if (isField()) {
			return field.getType().getSimpleName();
		}
		if (isMethod()) {
			return getter.getReturnType().getSimpleName();
		}
		return null;
	}

	@Override
	public String toString() {
		return "EditableObject: " + (isField() ? "Field " : "Method ") + name;
	}
}
