/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.synchronizer;

import java.util.HashSet;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.util.ChangeSupport;

/**
 *
 * @author Serdar
 */
public abstract class ModelSynchronizer {

    private final Set<ModelSynchronizerClient> listenerSet = new HashSet<ModelSynchronizerClient>();

    public final synchronized void modelChanged(ModelSynchronizerClient client) {
        for (ModelSynchronizerClient modelSynchronizerClient : listenerSet) {
            if (!modelSynchronizerClient.equals(client)) {
                modelSynchronizerClient.updateModel();
            }
        }
    }

    public final synchronized ModelSynchronizerClient create(ChangeListener changeListener) {
        ModelSynchronizerClient modelSynchronizerClient = new ModelSynchronizerClient(changeListener, ModelSynchronizer.this);
        listenerSet.add(modelSynchronizerClient);
        return modelSynchronizerClient;
    }

    public static class ModelSynchronizerClient {

        private final ChangeSupport changeSupport = new ChangeSupport(this);
        private final ChangeListener listener;
        private final ModelSynchronizer synchornizer;

        private ModelSynchronizerClient(ChangeListener listener, ModelSynchronizer synchronizer) {
            assert listener != null;
            assert synchronizer != null;
            this.listener = listener;
            this.synchornizer = synchronizer;
            changeSupport.addChangeListener(listener);
        }

        public void modelChanged() {
            synchornizer.modelChanged(ModelSynchronizerClient.this);
        }

        protected void updateModel() {
            listener.stateChanged(new ChangeEvent(synchornizer));
        }
    }
}
