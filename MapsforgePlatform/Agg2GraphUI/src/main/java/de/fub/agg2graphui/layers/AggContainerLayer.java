/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui.layers;

import de.fub.agg2graph.agg.AggConnection;
import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.agg.tiling.DefaultCachingStrategy;
import de.fub.agg2graph.agg.tiling.Tile;
import de.fub.agg2graph.ui.gui.RenderingOptions;
import de.fub.agg2graphui.controller.AbstractLayer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Serdar
 */
public class AggContainerLayer extends AbstractLayer<AggContainer> {

    public AggContainerLayer() {
        super("Aggregation Layer", "Aggregation Layer", new RenderingOptions());
        getRenderingOptions().setColor(new Color(38, 36, 5)); // black
        getRenderingOptions().setRenderingType(RenderingOptions.RenderingType.ALL);
        getRenderingOptions().setzIndex(3);
        getRenderingOptions().setOpacity(1);
    }

    @Override
    protected void drawDrawables(Graphics2D g2, Rectangle rectangle) {

        for (AggContainer container : getItemList()) {
            Rectangle2D.Double visibleArea = getLayerManager().getGpsArea();
            if (visibleArea != null) {
                // draw background (tile sizes)
                List<Tile<AggNode>> tiles = ((DefaultCachingStrategy) container.getCachingStrategy()).getTm().clipTiles(visibleArea);
                if (tiles != null) {
                    // draw them
                    g2.setColor(Color.WHITE);
                    g2.setStroke(getRenderingOptions().getStroke(16)); // TODO debug code
                    for (Tile<AggNode> tile : tiles) {
                        // if (!tile.getID().equals("0-4-8-2-4-3-5-5-1-2"))
                        // {
                        // continue;
                        // }
                        Rectangle2D.Double size = new Rectangle2D.Double(
                                tile.getSize().x, tile.getSize().y,
                                tile.getSize().width, tile.getSize().height);
                        Rectangle2D.Double projected;
                        if (!tile.isRoot()) {
                            // get projected corners
                            projected = projectRect(size);
                            // System.out.println(projected);

                            // fill
                            g2.setColor(tile.isLoaded ? new Color(1f, 0f, 0f, 0.05f) : new Color(0f, 0f, 1f, 0.05f));
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
//                            g2.setColor(Color.BLACK);
//                            g2.drawString(tile.getID(),
//                                    (int) projected.x + 40,
//                                    (int) projected.y + 40);
                        } else {
                            // // size.x = -89;
                            // // size.y = -179;
                            // // projected = projectRect(size);
                            // // System.out.println(projected);
                            // projected = new Rectangle2D.Double();
                            // // TODO change fix values!
                            // projected.x = -1.992618885199597E7;
                            // projected.y = -3.024097195838617E7;
                            // projected.width = 4E7;
                            // projected.height = 6E7;
                        }
                    }
                }

                // retrieve all points and connections in drawing area (and
                // few
                // around to make sure we catch all connections)
                double newWidth = visibleArea.width + 2 * CLIPPING_AREA_BORDER;
                double newHeight = visibleArea.height + 2 * CLIPPING_AREA_BORDER;
                // TODO clone before!
                visibleArea.setRect(visibleArea.x - (newWidth - visibleArea.width) / 2, visibleArea.y - (newHeight - visibleArea.height) / 2, newWidth, newHeight);

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
                    RenderingOptions roHighWeight = getRenderingOptions();
                    RenderingOptions roLowWeight = getRenderingOptions();
                    if (getLayerManager().isRenderWeight()) {
                        roHighWeight = getRenderingOptions().getCopy();
                        roHighWeight.setColor(Color.BLACK);
                        roLowWeight = getRenderingOptions().getCopy();
                        roLowWeight.setColor(new Color(95, 95, 95));
                    }
                    // first paint all connections
                    RenderingOptions lineRo = null;
                    float lineWidth = 1;
                    for (AggConnection conn : conns) {
                        if (getLayerManager().isRenderWeight()) {
                            lineRo = conn.getWeight() >= AGG_CONTAINER_WEIGHT_LIMIT ? roHighWeight : roLowWeight;
                            lineWidth = conn.getWeight();
                        } else {
                            // render avgDist
                            lineRo = getRenderingOptions();
                            lineWidth = Math.max(1, (int) conn.getAvgDist());
                        }
                        drawLine(conn.getFrom(), conn.getTo(), lineRo, lineWidth);
                    }

                    // then paint all nodes
                    for (AggNode node : nodes) {
                        drawPoint(node, getRenderingOptions());
                    }
                }
            }
        }
    }
}
