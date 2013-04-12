/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.synchronizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.util.ChangeSupport;
import org.openide.util.WeakSet;

/**
 *
 * @author Serdar
 */
public abstract class ModelSynchronizer {

    // set for weak references
    private final WeakSet<ModelSynchronizerClient> listenerSet = new WeakSet<ModelSynchronizerClient>();
    // map to hold order information in which the mscs are created
    private final SortedMap<Integer, ModelSynchronizerClient> listenerMap = new TreeMap<Integer, ModelSynchronizerClient>();
    private final Object MUTEX = new Object();
    private final HashMap<ChangeListener, ModelSynchronizerClient> listener = new HashMap<ChangeListener, ModelSynchronizerClient>();

    public final void modelChanged(ModelSynchronizerClient client) {
        synchronized (MUTEX) {
            // get current msc from the map
            HashSet<ModelSynchronizerClient> hashSet = new HashSet<ModelSynchronizerClient>(listenerMap.values());

            // filter all mscs that are already GC
            for (ModelSynchronizerClient modelSynchronizerClient : hashSet) {
                if (!listenerSet.contains(modelSynchronizerClient)) {
                    listenerMap.values().remove(modelSynchronizerClient);
                }
            }

            // iterate in order through all mscs
            for (Entry<Integer, ModelSynchronizerClient> entry : listenerMap.entrySet()) {
                if (!entry.getValue().equals(client)) {
                    entry.getValue().updateModel();
                }
            }
        }
    }

    public final ModelSynchronizerClient create(ChangeListener changeListener) {
        synchronized (MUTEX) {
            if (!listener.containsKey(changeListener)) {
                ModelSynchronizerClient modelSynchronizerClient = new ModelSynchronizerClient(changeListener, ModelSynchronizer.this);
                listenerSet.add(modelSynchronizerClient);
                listenerMap.put(listenerSet.size() - 1, modelSynchronizerClient);
                listener.put(changeListener, modelSynchronizerClient);
            }
            return listener.get(changeListener);
        }
    }

    protected void synchronize() {
        updateSource();
    }

    public abstract void updateSource();

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

        public void modelChangedFromGui() {
            synchornizer.updateSource();
        }

        public void modelChangedFromSource() {
            synchornizer.modelChanged(ModelSynchronizerClient.this);

        }

        protected void updateModel() {
            listener.stateChanged(new ChangeEvent(synchornizer));
        }
    }
}
