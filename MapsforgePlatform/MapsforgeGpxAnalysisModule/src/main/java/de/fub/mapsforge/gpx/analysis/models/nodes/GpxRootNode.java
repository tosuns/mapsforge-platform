/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.gpx.analysis.models.nodes;

import de.fub.mapsforge.gpx.analysis.models.factories.TrackNodeFactory;
import de.fub.gpxmodule.xml.Gpx;
import java.util.concurrent.Callable;
import org.openide.nodes.Children;

/**
 *
 * @author Serdar
 */
public class GpxRootNode extends CustomNode {

    public GpxRootNode(final Gpx gpx) {
        super(Children.createLazy(new Callable<Children>() {
            @Override
            public Children call() throws Exception {
                return gpx.getTrk().isEmpty() ? Children.LEAF : Children.create(new TrackNodeFactory(gpx.getTrk()), true);
            }
        }));
    }
}
