/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference;

import de.fub.mapsforge.project.detector.model.pipeline.AbstractDetectorProcess;
import de.fub.mapforgeproject.api.process.AbstractProcess;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.util.Map;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Serdar
 */
public abstract class AbstractInferenceModel extends AbstractDetectorProcess<Object, Map<String, FileObject>> {

    protected final ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;

    public AbstractInferenceModel(Detector detector) {
        super(detector);
        modelSynchronizerClient = detector.create(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                reinit();
            }
        });
    }

    private void reinit() {
    }
}
