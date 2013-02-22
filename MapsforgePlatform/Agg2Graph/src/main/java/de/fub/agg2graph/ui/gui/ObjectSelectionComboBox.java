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
