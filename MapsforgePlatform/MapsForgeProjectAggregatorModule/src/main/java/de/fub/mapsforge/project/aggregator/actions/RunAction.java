/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.actions;

import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.aggregator.pipeline.AggregateProcessPipeline;
import de.fub.mapsforge.project.models.Aggregator;
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
        id = "de.fub.mapsforge.project.aggregator.actions.RunAction")
@ActionRegistration(
        displayName = "#CTL_RunAction",
        lazy = false)
@ActionReference(path = "Loaders/text/aggregationbuilder+xml/Actions", position = 250, separatorAfter = 275)
@Messages("CTL_RunAction=Run")
public final class RunAction extends AbstractAction implements Presenter.Popup {

    private static final long serialVersionUID = 1L;
    private final Lookup context;

    public RunAction() {
        this(Utilities.actionsGlobalContext());
    }

    public RunAction(Lookup lookup) {
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
        if (aggregator != null && !aggregator.getSourceList().isEmpty()) {
            AggregateProcessPipeline pipeline = aggregator.getPipeline();
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
