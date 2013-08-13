/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.factories.inference;

import de.fub.maps.project.detector.model.inference.AbstractInferenceModel;
import de.fub.utilsmodule.node.CustomAbstractnode;
import java.util.concurrent.Callable;
import org.openide.nodes.Children;

/**
 *
 * @author Serdar
 */
public class ProcessHandlerRootNode extends CustomAbstractnode {

    public ProcessHandlerRootNode(final AbstractInferenceModel inferenceModel) {
        super(Children.createLazy(new Callable<Children>() {
            @Override
            public Children call() throws Exception {
                return inferenceModel != null ? Children.create(new ProcessHandlerNodeFactory(inferenceModel), true) : Children.LEAF;
            }
        }));
    }
}
