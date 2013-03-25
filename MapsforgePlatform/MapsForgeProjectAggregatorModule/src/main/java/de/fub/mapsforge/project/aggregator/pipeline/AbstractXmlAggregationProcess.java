/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline;

import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptor;
import de.fub.mapsforge.project.models.Aggregator;
import de.fub.mapsforge.project.utils.AggregatorUtils;
import java.io.IOException;
import org.openide.util.Exceptions;

/**
 *
 * This abstract class uses the fully qualified class name to search for the
 * process descrption xml file to create its ProcessDescriptor. The xml file
 * must have the same simple name as this class.
 *
 * @author Serdar
 */
public abstract class AbstractXmlAggregationProcess<I, O> extends AbstractAggregationProcess<I, O> {

    public AbstractXmlAggregationProcess(Aggregator aggregator) {
        super(aggregator);
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor desc = null;
        try {
            desc = AggregatorUtils.getProcessDescriptor(getClass());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return desc;
    }
}
