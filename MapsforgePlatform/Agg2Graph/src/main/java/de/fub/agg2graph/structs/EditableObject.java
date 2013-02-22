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
