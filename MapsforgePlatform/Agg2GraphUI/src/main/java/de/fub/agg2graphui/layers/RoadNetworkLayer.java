/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui.layers;

import de.fub.agg2graph.roadgen.Road;
import static de.fub.agg2graph.roadgen.Road.RoadType.PRIMARY;
import static de.fub.agg2graph.roadgen.Road.RoadType.SECONDARY;
import static de.fub.agg2graph.roadgen.Road.RoadType.TERTIARY;
import de.fub.agg2graph.roadgen.RoadNetwork;
import de.fub.agg2graph.structs.ILocation;
import de.fub.agg2graph.ui.gui.RenderingOptions;
import de.fub.agg2graphui.controller.AbstractLayer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

/**
 *
 * @author Serdar
 */
public class RoadNetworkLayer extends AbstractLayer<RoadNetwork> {

    public RoadNetworkLayer() {
        super("Roads", "Roads", new RenderingOptions());
        getOptions().setRenderingType(RenderingOptions.RenderingType.ALL);
        getOptions().setzIndex(5);
        getOptions().setOpacity(1);
        getOptions().setStrokeBaseWidthFactor(1.5f);
    }

    @Override
    protected void drawDrawables(List<Drawable> drawables, Graphics2D graphics, Rectangle rectangle) {
        for (RoadNetwork roadNetwork : getItemList()) {
            RenderingOptions roPrimary = getOptions().getCopy();
            RenderingOptions roSecondary = getOptions().getCopy();
            RenderingOptions roTertiary = getOptions().getCopy();
            roPrimary.setColor(new Color(219, 37, 37)); // red
            roSecondary.setColor(new Color(253, 143, 0)); // orange
            roTertiary.setColor(new Color(221, 255, 68)); // yellow
            // render roads
            RenderingOptions roInternal = getOptions();
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
                    default:
                        break;
                }
                List<? extends ILocation> nodes = r.getNodes();
                for (int i = 1; i < nodes.size(); i++) {
                    drawLine(nodes.get(i - 1),
                            nodes.get(i),
                            roInternal, 1,
                            (i == nodes.size() - 1 && r.isOneWay()));
                }
            }
        }
    }
}
