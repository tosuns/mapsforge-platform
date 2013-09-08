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
package de.fub.maps.project.aggregator.actions;

import de.fub.maps.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.maps.project.models.Aggregator;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import org.openide.cookies.OpenCookie;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Serdar
 */
public class DelegateAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private transient final List<AbstractAggregationProcess<?, ?>> processes;
    private transient RequestProcessor requestProcessor = new RequestProcessor(DelegateAction.class.getName());
    private transient final Aggregator aggregator;
    private static final Logger LOG = Logger.getLogger(DelegateAction.class.getName());

    public DelegateAction(Aggregator aggregator, AbstractAggregationProcess<?, ?> process) {
        this(aggregator, process.getName(), process);
    }

    public DelegateAction(Aggregator aggregator, String name, AbstractAggregationProcess<?, ?>... processes) {
        super(name);
        this.processes = Arrays.asList(processes);
        this.aggregator = aggregator;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OpenCookie openCookie = aggregator.getDataObject().getLookup().lookup(OpenCookie.class);
        if (openCookie != null) {
            openCookie.open();
        }
        requestProcessor.post(new Runnable() {
            @Override
            public void run() {
                aggregator.start(processes);
            }
        });
    }
}
