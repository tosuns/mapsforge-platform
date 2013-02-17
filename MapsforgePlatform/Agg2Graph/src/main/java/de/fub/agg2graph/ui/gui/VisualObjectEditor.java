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
package de.fub.agg2graph.ui.gui;

import de.fub.agg2graph.structs.ClassObjectEditor;
import de.fub.agg2graph.structs.EditableObject;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class VisualObjectEditor extends JPanel {
	private static final long serialVersionUID = 2126207635038022187L;
	private ClassObjectEditor coe;

	public VisualObjectEditor(Object object) {
		this.coe = new ClassObjectEditor(object);
		createObjects();
	}

	public VisualObjectEditor(Object object, List<String> exceptions) {
		this.coe = new ClassObjectEditor(object, exceptions);
		createObjects();
	}

	public VisualObjectEditor(ClassObjectEditor coe) {
		this.coe = coe;
		createObjects();
	}

	public void createObjects() {
		setLayout(new GridBagLayout());
		// headline
		JLabel headerLabel = new JLabel(coe.getObject().getClass()
				.getSimpleName()
				+ ": ");
		headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
		headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 0;
		gbc.weightx = 1;
		add(headerLabel, gbc);

		// make the jtable rows
		DefaultTableModel model = new MyTableModel();
		model.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getColumn() > 0 && e.getFirstRow() == e.getLastRow()) {
					EditableObject changedObject = coe.getEditableObjects()
							.get(e.getFirstRow());
					String value = String.valueOf(((TableModel) e.getSource())
							.getValueAt(e.getFirstRow(), e.getColumn()));
					setValue(changedObject, value);
				}
			}
		});
		JTable table = new MyJTable();
		table.setRowHeight(30);
		table.setModel(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridy = 1;
		add(table, gbc);
		for (EditableObject eo : coe.getEditableObjects()) {
			model.addRow(new Object[] { eo.name, eo.value });
		}
	}

	public void setValue(String settingName, String value) {
		for (EditableObject editableObject : coe.getEditableObjects()) {
			if (editableObject.name.equals(settingName)) {
				setValue(editableObject, value);
			}
		}
	}

	public void setValue(EditableObject objectToChange, String value) {
		Object object = coe.getObject();
		if (objectToChange.isField()) {
			Field f = objectToChange.field;
			System.out.println("setting value of field " + f.getName() + " to "
					+ value);
			try {
				Class<?> type = f.getType();
				// System.out.println(type.getName());
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
					// System.out.println(f.get(object));
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

	public class MyTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 8285823978268881055L;

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return column > 0;
		}
	}

	public class MyJTable extends JTable {

		private static final long serialVersionUID = -2400773783494142878L;

		public MyJTable() {
			super();
		}

		@Override
		public TableCellRenderer getCellRenderer(int row, int column) {
			if (column == 0) {
				return super.getDefaultRenderer(String.class);
			}
			// fix rending when null (usually indicates this should not be a
			// setting to expose to the user)
			if (coe.getEditableObjects().get(row).value == null) {
				System.out.println(coe.getEditableObjects().get(row).value);
				return super.getDefaultRenderer(String.class);
			}
			return super
					.getDefaultRenderer(coe.getEditableObjects().get(row).value
							.getClass());
		}

		@Override
		public TableCellEditor getCellEditor(int row, int column) {
			if (column == 0) {
				return super.getDefaultEditor(String.class);
			}
			return super
					.getDefaultEditor(coe.getEditableObjects().get(row).value
							.getClass());
		}

	}
}
