/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.aggregator.pipeline;

import de.fub.maps.project.aggregator.xml.ProcessDescriptor;
import de.fub.maps.project.utils.AggregatorUtils;
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

    /**
     * Creates the ProcessDescriptor via an xml file, which is located in the
     * corresponding resource package. if the ProcessDescriptor could not be
     * created via the xml file, an empty ProcessDescriptor unit will be
     * returned.
     *
     * @return ProcessDescriptor instance.
     */
    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor desc = new ProcessDescriptor();
        try {
            desc = AggregatorUtils.getProcessDescriptor(getClass());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return desc;
    }
}
