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

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ObjectSelectionComboBox extends JComboBox {

	private static final long serialVersionUID = 1L;
	private final Map<Object, String> texts = new HashMap<Object, String>(10);

	public ObjectSelectionComboBox() {
		super();
		setRenderer(new ComboBoxRenderer(texts));
	}

	public void addItem(Object item, String name) {
		texts.put(item, name);
		addItem(item);
	}

	private class ComboBoxRenderer extends JLabel implements ListCellRenderer {

		private static final long serialVersionUID = 1L;
		private final Map<Object, String> textMap;
		private final DefaultListCellRenderer dlcr = new DefaultListCellRenderer();

		public ComboBoxRenderer(Map<Object, String> textMap) {
			this.textMap = textMap;
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			return dlcr.getListCellRendererComponent(list, textMap.get(value),
					index, isSelected, cellHasFocus);
		}
	}
}
