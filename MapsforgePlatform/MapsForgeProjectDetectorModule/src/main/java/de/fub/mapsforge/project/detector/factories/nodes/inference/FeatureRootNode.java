/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories.nodes.inference;

import de.fub.mapsforge.project.detector.factories.inference.FeatureNodeChildFactory;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import de.fub.utilsmodule.node.CustomAbstractnode;
import java.util.concurrent.Callable;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_Feature_Root_Node_Name=Features",
    "CLT_Feature_Root_Node_Description=All Features that this inference model currently contains."
})
public class FeatureRootNode extends CustomAbstractnode {

    public FeatureRootNode(final Detector detector) {
        super(Children.createLazy(new Callable<Children>() {
            @Override
            public Children call() throws Exception {
                return detector != null ? Children.create(new FeatureNodeChildFactory(detector), true) : Children.LEAF;
            }
        }));
        setDisplayName(Bundle.CLT_Feature_Root_Node_Name());
        setShortDescription(Bundle.CLT_Feature_Root_Node_Description());
    }
}
