/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fub.utilsmodule.synchronizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.util.ChangeSupport;
import org.openide.util.WeakSet;

/**
 * Model Synchronizer to synchonizer the visual representation with the
 * underlying fileObject.
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

    /**
     * Notifies all clients, except the one which caused the notification.
     *
     * @param client
     */
    public final void modelChanged(ModelSynchronizerClient client) {
        final List<Entry<Integer, ModelSynchronizerClient>> clients = new ArrayList<Entry<Integer, ModelSynchronizerClient>>();
        synchronized (MUTEX) {
            // get current msc from the map
            HashSet<ModelSynchronizerClient> hashSet = new HashSet<ModelSynchronizerClient>(listenerMap.values());

            // filter all mscs that are already GC
            for (ModelSynchronizerClient modelSynchronizerClient : hashSet) {
                if (!listenerSet.contains(modelSynchronizerClient)) {
                    listenerMap.values().remove(modelSynchronizerClient);
                }
            }
            clients.addAll(listenerMap.entrySet());
        }

        // iterate in order through all mscs
        for (Entry<Integer, ModelSynchronizerClient> entry : clients) {
            if (!entry.getValue().equals(client)) {
                entry.getValue().updateModel();
            }
        }

    }

    /**
     * Creates a ModelSynchonizerClient for the specified ChangeListener.
     *
     * @param changeListener
     * @return ModelSynchronizerClient instance.
     */
    public final ModelSynchronizerClient create(ChangeListener changeListener) {
        if (!listener.containsKey(changeListener)) {
            synchronized (MUTEX) {
                ModelSynchronizerClient modelSynchronizerClient = new ModelSynchronizerClient(changeListener, ModelSynchronizer.this);
                listenerSet.add(modelSynchronizerClient);
                listenerMap.put(listenerSet.size() - 1, modelSynchronizerClient);
                listener.put(changeListener, modelSynchronizerClient);
            }
        }
        return listener.get(changeListener);
    }

    protected void synchronize() {
        updateSource();
    }

    /**
     * Updated the underlying object, i.e. a fileObject.
     */
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
