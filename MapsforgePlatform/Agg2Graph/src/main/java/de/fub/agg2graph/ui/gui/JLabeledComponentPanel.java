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
