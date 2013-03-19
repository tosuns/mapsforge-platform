/*******************************************************************************
    Copyright (c) 2012 Johannes Mitlmeier.
 
    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA

 ******************************************************************************/
package de.fub.agg2graph.ui.gui.jmv;

import de.fub.agg2graph.agg.AggConnection;
import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.agg.tiling.DefaultCachingStrategy;
import de.fub.agg2graph.agg.tiling.Tile;
import de.fub.agg2graph.roadgen.Intersection;
import de.fub.agg2graph.roadgen.Road;
import de.fub.agg2graph.roadgen.RoadNetwork;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.structs.GPSTrack;
import de.fub.agg2graph.structs.Hideable;
import de.fub.agg2graph.structs.ILocation;
import de.fub.agg2graph.structs.XYPoint;
import de.fub.agg2graph.ui.gui.IRenderingPanel;
import de.fub.agg2graph.ui.gui.LayerManager;
import de.fub.agg2graph.ui.gui.RenderingOptions;
import de.fub.agg2graph.ui.gui.RenderingOptions.LabelRenderingType;
import de.fub.agg2graph.ui.gui.RenderingOptions.RenderingType;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JComponent;
import org.openstreetmap.gui.jmapviewer.Coordinate;

public class Layer implements Hideable {
	private RenderingOptions options;
	private final List<HashMap<Object, RenderingOptions>> objects = new ArrayList<HashMap<Object, RenderingOptions>>();
	private String name;
	private String description;
	private final List<IRenderingPanel> panels = new ArrayList<IRenderingPanel>();
	private LayerManager layerManager;
	private BufferedImage image;
	private int drawnPointsCounter = 0;
	private boolean visible = true;
	private final int MAX_VISIBLE_POINTS_INTELLIGENT_LABELS = 7;
	private final int MAX_VISIBLE_POINTS_INTELLIGENT_POINT = 10;
	private final double CLIPPING_AREA_BORDER = 5000;

	private List<DrawObject> drawObjects;
	private Double lastGpsArea;

	public Layer(String name, RenderingOptions ro) {
		this.name = name;
		this.options = ro;
	}

	public Layer(String name, String description, RenderingOptions ro) {
		this.name = name;
		this.description = description;
		this.options = ro;
	}

	public LayerManager getLayerManager() {
		return layerManager;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setLayerManager(LayerManager layerManager) {
		this.layerManager = layerManager;
	}

	public List<IRenderingPanel> getPanels() {
		return panels;
	}

	public List<HashMap<Object, RenderingOptions>> getObjects() {
		return objects;
	}

	public void addPanel(IRenderingPanel panel) {
		if (!panels.contains(panel)) {
			panels.add(panel);
		}
	}

	public void addObject(Object object) {
		addObject(object, null);
	}

	public void addObject(Object object, RenderingOptions options) {
		HashMap<Object, RenderingOptions> map = new HashMap<Object, RenderingOptions>();
		map.put(object, options);
		objects.add(map);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RenderingOptions getOptions() {
		return options;
	}

	public void setOptions(RenderingOptions options) {
		this.options = options;
	}

	@Override
	public String toString() {
		return String.format("Layer \"%s\", #panels=%d", name, panels.size());
	}

	private void drawLine(Graphics2D g2, ILocation location1,
			ILocation location2, RenderingOptions ro) {
		drawLine(g2, location1, location2, ro, 1);
	}

	private void drawLine(Graphics2D g2, ILocation location1,
			ILocation location2, RenderingOptions ro, float weightFactor) {
		drawLine(g2, location1, location2, ro, weightFactor, true);
	}

	private void drawLine(Graphics2D g2, ILocation location1,
			ILocation location2, RenderingOptions ro, float weightFactor,
			boolean directed) {
		if (location1 == null || location2 == null
				|| ro.getRenderingType() == RenderingType.POINTS) {
			return;
		}

		// make sure we only render what's visible
		java.awt.Point p1 = getLayerManager().getMainPanel().getMapPosition(
				location1.getLat(), location1.getLon(), false);
		java.awt.Point p2 = getLayerManager().getMainPanel().getMapPosition(
				location2.getLat(), location2.getLon(), false);
		drawObjects.add(new Line(new XYPoint(location1.getID(), p1.x, p1.y),
				new XYPoint(location2.getID(), p2.x, p2.y), ro, weightFactor,
				directed));
	}

	private void drawPoint(Graphics2D g2, ILocation location,
			RenderingOptions ro) {
		if (location == null || ro.getRenderingType() == RenderingType.LINES) {
			return;
		}

		// make sure we only render what's visible
		java.awt.Point mapPosition = getLayerManager().getMainPanel()
				.getMapPosition(
						new Coordinate(location.getLat(), location.getLon()),
						true);
		if (mapPosition == null) {
			return;
		}
		drawObjects.add(new Point(new XYPoint(location.getID(), mapPosition.x,
				mapPosition.y), ro));
		drawnPointsCounter++;
	}

	public boolean repaint() {
		return repaint(false);
	}

	@SuppressWarnings("unchecked")
	public boolean repaint(boolean force) {
		if (layerManager.getSize() == null) {
			return false;
		}
		drawObjects = new ArrayList<DrawObject>();
		// GraphicsEnvironment env = GraphicsEnvironment
		// .getLocalGraphicsEnvironment();
		// GraphicsDevice device = env.getDefaultScreenDevice();
		// GraphicsConfiguration config = device.getDefaultConfiguration();
		// image = config.createCompatibleImage(layerManager.getSize().width,
		// layerManager.getSize().height, Transparency.TRANSLUCENT);
		image = new BufferedImage(layerManager.getSize().width,
				layerManager.getSize().height, BufferedImage.TYPE_INT_ARGB_PRE);
		if (objects.isEmpty() || !isVisible()) {
			return true;
		}
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// create font for labels
		Font labelFont = new Font(Font.SERIF, Font.PLAIN, 76);

		// queue draw operations
		drawnPointsCounter = 0;
		List<HashMap<Object, RenderingOptions>> loopCopyObjects = new ArrayList<HashMap<Object, RenderingOptions>>();
		loopCopyObjects.addAll(objects);
		for (Object loopObject : loopCopyObjects) {
			HashMap<Object, RenderingOptions> myMap = (HashMap<Object, RenderingOptions>) loopObject;
			Object object = myMap.keySet().toArray()[0];
			RenderingOptions ro = options;
			if (myMap.get(object) != null) {
				ro = myMap.get(object);
			}
			if (ro.getRenderingType() == RenderingType.NONE) {
				continue;
			}

			if (object instanceof AggContainer) {
				AggContainer container = (AggContainer) object;
				Rectangle2D.Double visibleArea = layerManager.getGpsArea();
				if (visibleArea != null) {
					// draw background (tile sizes)
					List<Tile<AggNode>> tiles = ((DefaultCachingStrategy) container
							.getCachingStrategy()).getTm().clipTiles(
							visibleArea);
					if (tiles != null) {
						// draw them
						g2.setColor(Color.WHITE);
						g2.setStroke(ro.getStroke(16));
						for (Tile<AggNode> tile : tiles) {
							Rectangle2D.Double size = new Rectangle2D.Double(
									tile.getSize().x, tile.getSize().y,
									tile.getSize().width, tile.getSize().height);
							Rectangle2D.Double projected;
							if (!tile.isRoot()) {
								// get projected corners
								projected = projectRect(size);

								// fill
								g2.setColor(tile.isLoaded ? new Color(1f, 0f,
										0f, 0.05f) : new Color(0f, 0f, 1f,
										0.05f));
								g2.fillRect((int) projected.x,
										(int) projected.y,
										(int) projected.width,
										(int) projected.height);
								// draw rectangle
								g2.setColor(Color.BLACK);
								g2.drawRect((int) projected.x,
										(int) projected.y,
										(int) projected.width,
										(int) projected.height);
								// name
								g2.setColor(Color.BLACK);
								g2.drawString(tile.getID(),
										(int) projected.x + 40,
										(int) projected.y + 40);
							}
						}
					}

					/*
					 * retrieve all points and connections in drawing area (and
					 * few around to make sure we catch all connections)
					 */
					double newWidth = visibleArea.width + 2
							* CLIPPING_AREA_BORDER;
					double newHeight = visibleArea.height + 2
							* CLIPPING_AREA_BORDER;
					visibleArea.setRect(visibleArea.x
							- (newWidth - visibleArea.width) / 2, visibleArea.y
							- (newHeight - visibleArea.height) / 2, newWidth,
							newHeight);

					List<AggNode> nodes = container.getCachingStrategy()
							.clipRegion(visibleArea);
					if (nodes != null) {
						Set<AggConnection> conns = new HashSet<AggConnection>();
						for (AggNode node : nodes) {
							if (!node.isShallow()) {
								conns.addAll(node.getIn());
								conns.addAll(node.getOut());
							}
						}
						RenderingOptions roVisible = ro;
						RenderingOptions roHidden = ro;
						if (layerManager.renderWeight) {
							roVisible = ro.getCopy();
							roVisible.setColor( Color.BLACK);
							roHidden = ro.getCopy();
							roHidden.setColor( new Color(95, 95, 95));
						}
						// first paint all connections
						RenderingOptions lineRo = null;
						float lineWidth = 1;
						for (AggConnection conn : conns) {
							if (layerManager.renderWeight) {
								lineRo = conn.isVisible() ? roVisible
										: roHidden;
								lineWidth = conn.getWeight();
							} else {
								// render avgDist
								lineRo = ro;
								lineWidth = Math
										.max(1, (int) conn.getAvgDist());
							}
							drawLine(g2, conn.getFrom(), conn.getTo(), lineRo,
									lineWidth);
						}
						// then paint all nodes
						for (AggNode node : nodes) {
							drawPoint(g2, node, ro);
						}
					}
				}
			}
			if (object instanceof Set<?>) { // Set<Intersection>
				// render intersections
				RenderingOptions intersectionRo = ro.getCopy();
				intersectionRo.setStrokeBaseWidthFactor( layerManager.getSize().width / 50);
				intersectionRo.setStrokeBaseWidthFactor( 7 + 17 / 6 * (layerManager
						.getUi().getMainPanel().getZoom() - 12));
				for (Intersection i : (Set<Intersection>) object) {
					if (!i.isVisible()) {
						continue;
					}
					drawPoint(g2, i, intersectionRo);
				}
			}
			if (object instanceof RoadNetwork) {
				RoadNetwork roadNetwork = (RoadNetwork) object;
				RenderingOptions roPrimary = ro.getCopy();
				RenderingOptions roSecondary = ro.getCopy();
				RenderingOptions roTertiary = ro.getCopy();
				roPrimary.setColor (new Color(219, 37, 37)); // red
				roSecondary.setColor (new Color(253, 143, 0)); // orange
				roTertiary.setColor (new Color(221, 255, 68)); // yellow
				// render roads
				RenderingOptions roInternal = ro;
				for (Road r : roadNetwork.roads) {
					if (!r.isVisible()) {
						continue;
					}
					switch (r.getType()) {
					case PRIMARY:
						roInternal = roPrimary;
						break;
					case SECONDARY:
						roInternal = roSecondary;
						break;
					case TERTIARY:
						roInternal = roTertiary;
						break;
					}
					List<? extends ILocation> nodes = r.getNodes();
					for (int i = 1; i < nodes.size(); i++) {
						drawLine(g2, nodes.get(i - 1), nodes.get(i),
								roInternal, 1,
								(i == nodes.size() - 1 && r.isOneWay()));
					}
				}
			} else if (object instanceof GPSTrack) {
				GPSTrack track = (GPSTrack) object;
				for (GPSSegment segment : track) {
					GPSPoint lastPoint = null;
					for (GPSPoint point : segment) {
						if (lastPoint != null) {
							drawLine(g2, lastPoint, point, ro);
						}
						drawPoint(g2, point, ro);
						lastPoint = point;
					}
				}
			} else if (object instanceof GPSSegment) {
				GPSSegment segment = (GPSSegment) object;
				GPSPoint lastPoint = null;
				for (GPSPoint point : segment) {
					if (lastPoint != null) {
						drawLine(g2, lastPoint, point, ro);
					}
					drawPoint(g2, point, ro);
					lastPoint = point;
				}
			} else if (object instanceof GPSPoint) {
				GPSPoint point = (GPSPoint) object;
				drawPoint(g2, point, ro);
			} else if (object instanceof List<?>) {
				List<ILocation> points = (List<ILocation>) object;
				ILocation lastPoint = null;
				for (ILocation point : points) {
					if (lastPoint != null) {
						drawLine(g2, lastPoint, point, ro, 1, true);
					}
					drawPoint(g2, point, ro);
					lastPoint = point;
				}
			}
		}

		labelFont = labelFont.deriveFont((float) (32 - 3 * Math
				.log(drawnPointsCounter)));
		g2.setFont(labelFont);

		// actually draw
		Polygon arrowHead = new Polygon();
		arrowHead.addPoint((int) (-4 * layerManager.thicknessFactor),
				(int) (-8 * layerManager.thicknessFactor));
		arrowHead.addPoint(0, (int) (4 * layerManager.thicknessFactor));
		arrowHead.addPoint((int) (4 * layerManager.thicknessFactor),
				(int) (-8 * layerManager.thicknessFactor));
		List<DrawObject> internalLoopDrawObjects = new ArrayList<DrawObject>();
		internalLoopDrawObjects.addAll(drawObjects);
		for (DrawObject drawObject : internalLoopDrawObjects) {
			// set color
			if (drawObject == null) {
				continue;
			}
			RenderingOptions ro = drawObject.getRenderingOptions();
			Color lineColor = new Color((float) (ro.getColor().getRed() / 255.0),
					(float) (ro.getColor().getGreen() / 255.0),
					(float) (ro.getColor().getBlue() / 255.0), (float) ro.getOpacity());
			g2.setColor(lineColor);

			if (drawObject instanceof Point) {
				Point p = (Point) drawObject;
				// draw point
				if (ro.getRenderingType() == RenderingType.POINTS) {
					float width = ro.getStrokeBaseWidthFactor()
							* RenderingOptions.getBasicStroke().getLineWidth();
					g2.fillOval((int) (p.at.getX() - width / 2),
							(int) (p.at.getY() - width / 2), (int) width,
							(int) width);
				}
				// draw label
				// do not draw label if not fully opaque
				if (ro.getOpacity() < 1) {
					continue;
				}
				if (p.at.getID() == null
						|| ro.getLabelRenderingType() == LabelRenderingType.NEVER) {
					continue;
				}
				if (ro.getLabelRenderingType() == LabelRenderingType.INTELLIGENT
						&& drawnPointsCounter > MAX_VISIBLE_POINTS_INTELLIGENT_LABELS) {
					continue;
				}
				g2.drawString(p.at.getID(), (int) p.at.getX(),
						(int) (p.at.getY() + layerManager.getMainPanel()
								.getHeight() * 0.075));

			} else if (drawObject instanceof Line) {
				// draw line
				if (ro.getRenderingType() == RenderingType.POINTS) {
					continue;
				}
				Line l = (Line) drawObject;
				float width = l.weightFactor * layerManager.thicknessFactor;
				g2.setStroke(ro.getStroke(width));
				g2.drawLine((int) l.from.getX(), (int) l.from.getY(),
						(int) l.to.getX(), (int) l.to.getY());
				// make a nice arrow :) (code from
				// http://stackoverflow.com/a/3094933)
				if (ro.getRenderingType() == RenderingType.LINES) {
					continue;
				}
				if (ro.getRenderingType() == RenderingType.INTELLIGENT_ALL
						&& drawnPointsCounter > MAX_VISIBLE_POINTS_INTELLIGENT_POINT) {
					continue;
				}
				if (l.directed) {
					double angle = Math.atan2(l.to.getY() - l.from.getY(),
							l.to.getX() - l.from.getX());
					AffineTransform oldTx = g2.getTransform();
					AffineTransform tx = new AffineTransform(oldTx);
					tx.translate(l.to.getX(), l.to.getY());
					tx.rotate((angle - Math.PI / 2d));
					g2.setTransform(tx);
					g2.setStroke(ro.getStroke(layerManager.thicknessFactor));
					g2.drawLine(0, 0, (int) (6 + width * 2),
							(int) (-8 - width * 2));
					g2.drawLine(0, 0, (int) (-6 - width * 2),
							(int) (-8 - width * 2));
					g2.setTransform(oldTx);
				}
			}

		}

		return true;
	}

	public Rectangle2D.Double projectRect(Rectangle2D.Double source) {
		ILocation ul = new GPSPoint(source.x, source.y);
		ILocation lr = new GPSPoint(source.getMaxX(), source.getMaxY());
		source.x = ul.getX();
		source.y = ul.getY();
		source.width = lr.getX() - ul.getX();
		source.height = lr.getY() - ul.getY();
		return source;
	}

	public void clear() {
		objects.clear();
	}

	public interface DrawObject {
		public RenderingOptions getRenderingOptions();
	}

	public class Line implements DrawObject {
		public ILocation from;
		public ILocation to;
		public RenderingOptions renderingOptions;
		public float weightFactor = 1;
		public boolean directed = true;

		public Line(ILocation from, ILocation to, RenderingOptions ro,
				float weightFactor) {
			this.from = from;
			this.to = to;
			this.renderingOptions = ro;
			this.weightFactor = weightFactor;
		}

		public Line(ILocation from, ILocation to, RenderingOptions ro,
				float weightFactor, boolean directed) {
			this(from, to, ro, weightFactor);
			this.directed = directed;
		}

		@Override
		public RenderingOptions getRenderingOptions() {
			return renderingOptions;
		}
	}

	public class Point implements DrawObject {
		public ILocation at;
		public RenderingOptions renderingOptions;

		public Point(ILocation at, RenderingOptions ro) {
			this.at = at;
			this.renderingOptions = ro;
		}

		@Override
		public RenderingOptions getRenderingOptions() {
			return renderingOptions;
		}
	}

	public void paintToComponent(JComponent component, Graphics2D g2) {
		// something new, different?
		Rectangle2D.Double gpsArea = layerManager.getGpsArea();
		if (lastGpsArea == null || !lastGpsArea.equals(gpsArea)
				|| component.getSize().width > getImage().getWidth()
				|| component.getSize().height > getImage().getHeight()) {
			repaint(true);
			lastGpsArea = gpsArea;
		}
		// scale the image to the
		if (image == null) {
			return;
		}
		g2.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
