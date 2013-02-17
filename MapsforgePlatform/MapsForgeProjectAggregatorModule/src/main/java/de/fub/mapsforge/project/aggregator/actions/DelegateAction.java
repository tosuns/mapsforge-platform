/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.actions;

import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.models.Aggregator;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Serdar
 */
public class DelegateAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final List<AbstractAggregationProcess<?, ?>> processes;
    private RequestProcessor requestProcessor = new RequestProcessor(DelegateAction.class.getName());
    private final Aggregator aggregator;
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
        requestProcessor.post(new Runnable() {
            @Override
            public void run() {
                aggregator.start(processes);
            }
        });
    }
}
