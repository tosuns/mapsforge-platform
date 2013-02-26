/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.nodes;

import de.fub.mapsforge.project.detector.model.Detector;
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;

/**
 *
 * @author Serdar
 */
public class DetectorNode extends DataNode {

    public DetectorNode(Detector detector) {
        super(detector.getDataObject(), Children.LEAF);

    }
}
