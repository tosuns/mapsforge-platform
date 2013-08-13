/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
