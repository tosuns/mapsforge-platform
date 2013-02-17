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

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JPanel;

public class RenderingPanel extends JPanel implements IRenderingPanel {
	private static final long serialVersionUID = 1199223710435106007L;
	protected TestUI parentUi;
	protected List<Layer> layers = new ArrayList<Layer>();

	public RenderingPanel() {
	}

	public RenderingPanel(TestUI parentUi) {
		this();
		this.parentUi = parentUi;
	}

	@Override
	public void paintComponent(Graphics g) {
		if (getUi() == null) {
			return;
		}

		// set tooltip
		StringBuilder sb = new StringBuilder();
		for (Layer layer : layers) {
			sb.append(layer.getDescription()).append(", ");
		}
		sb.replace(sb.length() - 2, sb.length(), "");
		setToolTipText(sb.toString());

		// layer the layers ;-)
		Graphics2D g2 = (Graphics2D) g;
		double scaling = (double) getWidth()
				/ (double) getUi().getLayerManager().getSize().width;
		int width = getUi().getLayerManager().getSize().width;
		int height = getUi().getLayerManager().getSize().height;
		g2.scale(scaling, scaling);
		GradientPaint pat = new GradientPaint(10, 0, new Color(255, 255, 255),
				10, height, new Color(210, 210, 210), true);
		g2.setPaint(pat);
		g2.fillRect(0, 0, width, height);

		for (Layer layer : layers) {
			layer.paintToComponent(this, g2);
		}
	}

	@Override
	public List<Layer> getLayers() {
		return layers;
	}

	@Override
	public TestUI getUi() {
		return parentUi;
	}

	@Override
	public void setUi(TestUI ui) {
		this.parentUi = ui;
	}

	@Override
	public void addLayer(Layer layer) {
		if (!layers.contains(layer)) {
			layers.add(layer);
			Collections.sort(layers, new Comparator<Layer>() {

				@Override
				public int compare(Layer o1, Layer o2) {
					if (o1 == null || o1.getOptions() == null) {
						return 1;
					}
					if (o2 == null || o2.getOptions() == null) {
						return -1;
					}
					return o1.getOptions().getzIndex() - o2.getOptions().getzIndex();
				}
			});
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Layer layer : layers) {
			sb.append(layer.toString()).append(", ");
		}

		return String.format("RenderingPanel: [%s]", sb.toString());
	}
}
