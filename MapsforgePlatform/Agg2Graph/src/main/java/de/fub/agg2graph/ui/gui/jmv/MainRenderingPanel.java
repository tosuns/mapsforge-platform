/*******************************************************************************
    Copyright (c) 2012 Johannes Mitlmeier.
 
    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA

 ******************************************************************************/
package de.fub.agg2graph.ui.gui.jmv;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.openstreetmap.gui.jmapviewer.Coordinate;

import de.fub.agg2graph.input.Globals;
import de.fub.agg2graph.management.Statistics;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.ui.gui.IRenderingPanel;
import de.fub.agg2graph.ui.gui.LayerManager;
import de.fub.agg2graph.ui.gui.RenderingOptions.LabelRenderingType;

public class MainRenderingPanel extends OsmPanel implements IRenderingPanel {
	private static final long serialVersionUID = 1199223710435106007L;
	private int highlightIndex = 0;
	private int savedZIndex = Integer.MAX_VALUE;
	private boolean liveDragging = false;
	private boolean isBeingDragged = false;
	private boolean isDebugging = true;
	private boolean transparentHighlighting = true;
	private boolean showingLabels = false;
	protected TestUI parentUi;
	protected List<Layer> layers = new ArrayList<Layer>();
	private JMenu layerMenu;

	public MainRenderingPanel(final TestUI testUI) {
		this.parentUi = testUI;
		final MainRenderingPanel outerThis = this;
		final JPopupMenu contextMenu = new JPopupMenu();

		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				isBeingDragged = false;
				repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				isBeingDragged = true;
			}

			@Override
			public void mouseExited(MouseEvent e) {
				isBeingDragged = false;
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				isBeingDragged = false;
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		final MouseWheelListener osmWheelListener = getMouseWheelListeners()[0];
		removeMouseWheelListener(osmWheelListener);
		addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.isShiftDown()) {
					highlightLayer(e.getWheelRotation());
				} else {
					// push down
					osmWheelListener.mouseWheelMoved(e);
				}
			}
		});

		// context menu
		JMenuItem reset = new JMenuItem("Reset");
		reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showArea(getUi().dataBoundingBox);
			}
		});
		contextMenu.add(reset);
		JMenuItem repaint = new JMenuItem("Repaint");
		repaint.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getUi().getLayerManager().repaintAllLayers();
			}
		});
		contextMenu.add(repaint);
		JMenuItem thickness = new JMenuItem("Set thickness…");
		thickness.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String response = (String) JOptionPane.showInputDialog(
						testUI.frmTestui,
						"Line thickness factor:",
						"Settings",
						JOptionPane.QUESTION_MESSAGE,
						null,
						null,
						String.valueOf(getUi().getLayerManager().thicknessFactor));
				try {
					getUi().getLayerManager().thicknessFactor = Float
							.parseFloat(response);
				} catch (NumberFormatException ex) {

				} catch (NullPointerException ex) {

				}
				getUi().getLayerManager().repaintAllLayers();
			}
		});
		contextMenu.add(thickness);
		JMenuItem labels = new JMenuItem("Labels on/off");
		labels.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showingLabels = !showingLabels;
				for (Layer layer : getUi().getLayerManager().getLayers()) {
					if (showingLabels) {
						layer.getOptions().setLabelRenderingType( LabelRenderingType.ALWAYS);
					} else {
						layer.getOptions().setLabelRenderingType(LabelRenderingType.NEVER);
					}
				}
				outerThis.repaint();
			}
		});
		contextMenu.add(labels);
		JMenuItem debug = new JMenuItem("Debug on/off");
		debug.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				isDebugging = !isDebugging;
				Layer matching = getUi().getLayerManager().getLayer("matching");
				Layer merging = getUi().getLayerManager().getLayer("merging");
				matching.setVisible(isDebugging);
				merging.setVisible(isDebugging);
				getUi().getLayerManager().repaintAllLayers();
			}
		});
		contextMenu.add(debug);
		JMenuItem info = new JMenuItem("Map Info");
		info.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder text = new StringBuilder();
				text.append(String.format("Component size: [%d,%d]\n",
						getWidth(), getHeight()));
				LayerManager lm = getUi().getLayerManager();
				text.append(String
						.format("Data bounding box: lat[%.4f - %.4f], lon[%.4f - %.4f]\n",
								testUI.dataBoundingBox.getMinX(),
								testUI.dataBoundingBox.getMaxX(),
								testUI.dataBoundingBox.getMinY(),
								testUI.dataBoundingBox.getMaxY()));
				Rectangle2D.Double gpsArea = lm.getGpsArea();
				text.append(String.format(
						"GPS area: lat[%f - %f], lon[%f - %f]\n",
						gpsArea.getMinX(), gpsArea.getMaxX(),
						gpsArea.getMinY(), gpsArea.getMaxY()));
				Rectangle2D.Double projectionArea = lm.getProjectionArea();
				text.append(String.format(
						"Projection area: x[%f - %f], y[%f - %f]\n",
						projectionArea.getMinX(), projectionArea.getMaxX(),
						projectionArea.getMinY(), projectionArea.getMaxY()));
				JOptionPane.showMessageDialog(outerThis, text.toString());
			}
		});
		contextMenu.add(info);

		JMenu displayMenu = new JMenu("Print to system.out");
		contextMenu.add(displayMenu);
		JMenuItem inputData = new JMenuItem("input data");
		inputData.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(String.format("%d input segments",
						getUi().uiStepStorage.inputSegmentList.size()));
			}
		});
		displayMenu.add(inputData);
		JMenuItem cleanData = new JMenuItem("clean data");
		cleanData.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(String.format("%d clean segments",
						getUi().uiStepStorage.cleanSegmentList.size()));
			}
		});
		displayMenu.add(cleanData);
		JMenuItem aggDisplay = new JMenuItem("agg");
		aggDisplay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(getUi().uiStepStorage.getAggContainer()
						.toDebugString());
			}
		});
		displayMenu.add(aggDisplay);
		JMenuItem roadDisplay = new JMenuItem("road network");
		roadDisplay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(getUi().uiStepStorage.getRoadNetwork()
						.toDebugString());
			}
		});
		displayMenu.add(roadDisplay);
		JMenuItem stats = new JMenuItem("road statistics");
		stats.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Map<String, Double> data = Statistics.getData(parentUi
						.getUIStepStorage().getRoadNetwork());
				for (String key : data.keySet()) {
					System.out.println(key + ": " + data.get(key));
				}
			}
		});
		displayMenu.add(stats);

		JMenuItem weightDistSwitch = new JMenuItem(
				"Switch weight/average distance");
		weightDistSwitch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getUi().getLayerManager().renderWeight = !parentUi
						.getLayerManager().renderWeight;
				getUi().getLayerManager().repaintAllLayers();
			}
		});
		contextMenu.add(weightDistSwitch);
		// menu for switching visible layers
		layerMenu = new JMenu("Layers");
		contextMenu.add(layerMenu);
		setComponentPopupMenu(contextMenu);
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

		// update menu
		layerMenu.removeAll();
		for (final Layer l : layers) {
			JMenuItem layerItem = new JMenuItem(
					l.getDescription() != null ? l.getDescription()
							: l.getName());
			layerItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					l.setVisible(!l.isVisible());
					getUi().getLayerManager().repaintAllLayers();
				}
			});
			layerMenu.add(layerItem);
		}
	}

	private void highlightLayer(int wheelRotation) {
		if (savedZIndex != Integer.MAX_VALUE) {
			layers.get(highlightIndex).getOptions().setzIndex(savedZIndex);
		}
		highlightIndex = (highlightIndex + wheelRotation + layers.size())
				% layers.size();
		savedZIndex = layers.get(highlightIndex).getOptions().getzIndex();
		layers.get(highlightIndex).getOptions().setzIndex(999);

		if (transparentHighlighting) {
			for (Layer layer : layers) {
				layer.getOptions().setOpacity(0.3);
			}
			layers.get(highlightIndex).getOptions().setOpacity(1);
		}
		getUi().getLayerManager().repaintAllLayers();
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				0.5f));
		super.paintComponent(g);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

		// update size information for LayerManager
		if (getUi() == null) {
			return;
		}
		LayerManager lm = getUi().getLayerManager();
		Coordinate lt = getPosition(0, getHeight());
		GPSPoint leftTop = new GPSPoint(lt.getLat(), lt.getLon());
		Coordinate br = getPosition(getWidth(), 0);
		GPSPoint bottomRight = new GPSPoint(br.getLat(), br.getLon());
		Rectangle2D.Double gpsArea = new Rectangle2D.Double(leftTop.getLat(),
				leftTop.getLon(), bottomRight.getLat() - leftTop.getLat(),
				bottomRight.getLon() - leftTop.getLon());
		Rectangle2D.Double projectionArea = new Rectangle2D.Double(
				leftTop.getX(), leftTop.getY(), bottomRight.getX()
						- leftTop.getX(), bottomRight.getY() - leftTop.getY());
		lm.setArea(gpsArea, projectionArea);
		lm.setSize(getSize());

		if (!liveDragging && isBeingDragged) {
			return;
		}
		if (((TestUI) Globals.get("ui")).isShowLayers()) {
			paintLayers(g2);
			lm.mirrorToSmallPanels();
		}
	}

	public void paintLayers(Graphics2D g2) {
		if (getUi() == null) {
			return;
		}
		LayerManager lm = getUi().getLayerManager();
		if (getLayers().isEmpty() || lm.getGpsArea() == null) {
			return;
		}
		// paint the layers
		ArrayList<Layer> paintLayers = new ArrayList<Layer>();
		paintLayers.addAll(layers);
		// sort layers
		Collections.sort(paintLayers, new Comparator<Layer>() {

			@Override
			public int compare(Layer o1, Layer o2) {
				return o1.getOptions().getzIndex() - o2.getOptions().getzIndex();
			}

		});
		for (Layer layer : paintLayers) {
			layer.paintToComponent(this, g2);
		}
	}

	protected Color lighten(Color color, double strength) {
		int red = (int) ((color.getRed() * (1 - strength) / 255 + strength) * 255);
		int green = (int) ((color.getGreen() * (1 - strength) / 255 + strength) * 255);
		int blue = (int) ((color.getBlue() * (1 - strength) / 255 + strength) * 255);
		return new Color(red, green, blue);
	}
}
