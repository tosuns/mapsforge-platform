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

import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JLabeledComponentPanel extends JPanel {
	private static final long serialVersionUID = -7284443790393739469L;

	public JLabeledComponentPanel(JLabel label, JComponent comp) {
		super();
		this.setLayout(new FlowLayout());
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		add(label);
		add(comp);
		validate();
		repaint();
	}
}
