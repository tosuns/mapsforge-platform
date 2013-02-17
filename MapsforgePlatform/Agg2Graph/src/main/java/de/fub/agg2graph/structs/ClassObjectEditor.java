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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ClassObjectEditor {
	private Object object;
	private List<EditableObject> editableObjects = new ArrayList<EditableObject>(
			10);
	private List<String> exceptions = null;
	private String description;

	public ClassObjectEditor(Object object) {
		this(object, null, null);
	}

	public ClassObjectEditor(Object object, String description) {
		this(object, description, null);
	}

	public ClassObjectEditor(Object object, List<String> exceptions) {
		this(object, null, exceptions);
	}

	public ClassObjectEditor(Object object, String description,
			List<String> exceptions) {
		this.object = object;
		this.description = description;
		this.exceptions = exceptions;
		findEditableObjects();
	}

	public String getDescription() {
		if (description != null) {
			return description;
		}
		if (getObject() == null) {
			return "unknown";
		}
		return getObject().getClass().getName();
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<EditableObject> getEditableObjects() {
		return editableObjects;
	}

	public void setEditableObjects(List<EditableObject> editableObjects) {
		this.editableObjects = editableObjects;
	}

	public void findEditableObjects() {
		// get editable options (JavaBean-like)
		Class<? extends Object> c = getObject().getClass();
		Method[] methods = c.getDeclaredMethods();
		Field[] fields = c.getDeclaredFields();
		for (Field field : fields) {
			if (field.getModifiers() == Modifier.PUBLIC) {
				// System.out.println(field.getName());
				if (exceptions != null && exceptions.contains(field.getName())) {
					continue;
				}
				try {
					editableObjects.add(EditableObject.makeFromField(
							field.getName(), field.get(getObject()), field));
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				}
			}
		}
		// loop all getters and search for matching setters
		for (Method getter : methods) {
			if (getter.getModifiers() == Modifier.PUBLIC
					&& getter.getName().startsWith("get")) {
				// do we have a matching getter?
				String setterName = getter.getName().replaceAll("^get", "set");
				String baseName = getter.getName().replaceAll("^get", "");
				baseName = Character.toLowerCase(baseName.charAt(0))
						+ (baseName.length() > 1 ? baseName.substring(1) : "");
				if (exceptions != null && exceptions.contains(baseName)) {
					continue;
				}
				for (Method setter : methods) {
					if (setter.getModifiers() == Modifier.PUBLIC
							&& setter.getName().equals(setterName)) {
						try {
							Object value = getter.invoke(getObject());
							editableObjects.add(EditableObject.makeFromMethod(
									baseName, value, getter, setter));
						} catch (InvocationTargetException e) {
						} catch (IllegalArgumentException e) {
						} catch (IllegalAccessException e) {
						}
					}
				}

			}
		}
	}

	public List<String> getExceptions() {
		return exceptions;
	}

	public Object getObject() {
		return object;
	}

	public void setValue(String settingName, String value) {
		if (exceptions != null && exceptions.contains(settingName)) {
			return;
		}
		for (EditableObject editableObject : editableObjects) {
			if (editableObject.name.equals(settingName)) {
				setValue(editableObject, value);
			}
		}
	}

	public void setValue(EditableObject objectToChange, String value) {
		if (objectToChange.isField()) {
			Field f = objectToChange.field;
			System.out.println("setting value of field " + f.getName() + " to "
					+ value);
			try {
				Class<?> type = f.getType();
				if (type.getName().equals("int")) {
					f.set(object, Integer.parseInt(value));
				} else if (type.getName().equals("long")) {
					f.set(object, Long.parseLong(value));
				} else if (type.getName().equals("float")) {
					f.set(object, Float.parseFloat(value));
				} else if (type.getName().equals("double")) {
					f.set(object, Double.parseDouble(value));
				} else if (type.getName().equals("boolean")) {
					f.set(object, Boolean.parseBoolean(value));
				} else {
					f.set(object, value);
				}
			} catch (IllegalArgumentException e1) {
				System.out.println(String.format(
						"Error with value %s for type %s", value, f.getType()
								.getName()));
			} catch (IllegalAccessException e1) {
			}
		} else { // method...
			Method m = objectToChange.setter;
			System.out.println("setting value via setter " + m.getName()
					+ " to " + value);
			Class<?> type = null;
			try {
				type = m.getParameterTypes()[0];
				// System.out.println(type.getName());
				if (type.getName().equals("int")) {
					m.invoke(object, Integer.parseInt(value));
				} else if (type.getName().equals("long")) {
					m.invoke(object, Long.parseLong(value));
				} else if (type.getName().equals("float")) {
					m.invoke(object, Float.parseFloat(value));
				} else if (type.getName().equals("double")) {
					m.invoke(object, Double.parseDouble(value));
				} else if (type.getName().equals("boolean")) {
					m.invoke(object, Boolean.parseBoolean(value));
				} else {
					m.invoke(object, value);
				}
			} catch (IllegalArgumentException e1) {
				System.out.println(String.format("Error with value %s", value));
			} catch (IllegalAccessException e1) {
				System.out.println(String.format("Error with value %s", value));
			} catch (InvocationTargetException e1) {
				System.out.println(String.format("Error with value %s: %s",
						value, e1.getTargetException().getLocalizedMessage()));
			}
		}
	}
}
