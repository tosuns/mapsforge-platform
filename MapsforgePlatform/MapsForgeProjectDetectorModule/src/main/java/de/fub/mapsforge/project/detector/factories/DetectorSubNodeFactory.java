/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories;

import de.fub.mapsforge.project.detector.factories.nodes.InferenceModelNode;
import de.fub.mapsforge.project.detector.factories.nodes.PostProcessorsNode;
import de.fub.mapsforge.project.detector.factories.nodes.PreProcessorsNode;
import de.fub.mapsforge.project.detector.factories.nodes.datasets.DataSetFolderNode;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.xmls.DetectorDescriptor;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class DetectorSubNodeFactory extends ChildFactory<Node> implements ChangeListener {

    private final Detector detector;

    public DetectorSubNodeFactory(Detector detector) {
        this.detector = detector;

    }

    @Override
    protected boolean createKeys(List<Node> toPopulate) {
        DetectorDescriptor detectorDescriptor = detector.getDetectorDescriptor();
        if (detectorDescriptor != null) {
            toPopulate.add(new DataSetFolderNode(detector));
            toPopulate.add(new PreProcessorsNode(detector));
            toPopulate.add(new InferenceModelNode(detector));
            toPopulate.add(new PostProcessorsNode(detector));
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(Node node) {
        return new FilterNode(node);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        refresh(true);
    }
}
