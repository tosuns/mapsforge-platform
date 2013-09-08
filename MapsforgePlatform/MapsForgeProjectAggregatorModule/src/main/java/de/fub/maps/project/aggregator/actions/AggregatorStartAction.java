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
import de.fub.maps.project.aggregator.pipeline.AggregatorProcessPipeline;
import de.fub.maps.project.models.Aggregator;
import de.fub.maps.project.models.Aggregator.AggregatorState;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

@ActionID(
        category = "Aggregator",
        id = "de.fub.maps.project.aggregator.actions.AggregatorStartAction")
@ActionRegistration(
        displayName = "#CTL_RunAction",
        lazy = false)
@ActionReference(path = "Loaders/text/aggregationbuilder+xml/Actions", position = 250, separatorAfter = 275)
@Messages("CTL_RunAction=Start")
public final class AggregatorStartAction extends AbstractAction implements Presenter.Popup {

    private static final long serialVersionUID = 1L;
    private final Lookup context;

    public AggregatorStartAction() {
        this(Utilities.actionsGlobalContext());
    }

    public AggregatorStartAction(Lookup lookup) {
        this.context = lookup;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        // TODO use context
    }

    @Override
    public JMenuItem getPopupPresenter() {
        Aggregator aggregator = context.lookup(Aggregator.class);
        JMenu menu = new JMenu(Bundle.CTL_RunAction());
        if (aggregator != null
                && !aggregator.getSourceList().isEmpty()
                && aggregator.getAggregatorState() != AggregatorState.ERROR_NOT_EXECUTABLE) {
            AggregatorProcessPipeline pipeline = aggregator.getPipeline();
            DelegateAction delegateAction = null;
            for (int i = 0; i < pipeline.size(); i++) {
                AbstractAggregationProcess<?, ?> process = pipeline.get(i);
                delegateAction = new DelegateAction(aggregator, process);
                menu.add(new JMenuItem(delegateAction));
            }
            menu.add(new JPopupMenu.Separator());
            delegateAction = new DelegateAction(aggregator, "Run All", pipeline.getProcesses().toArray(new AbstractAggregationProcess<?, ?>[pipeline.getProcesses().size()]));
            menu.add(delegateAction);
        } else {
            menu.setEnabled(false);
        }
        return menu;
    }
}
