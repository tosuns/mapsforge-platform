/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.factories.nodes;

import de.fub.maps.project.api.process.Process;
import de.fub.maps.project.api.process.ProcessNode;

/**
 *
 * @author Serdar
 */
public class FilterNode extends ProcessNode {

    public FilterNode(Process<?, ?> process) {
        super(process);
        setDisplayName(process.getName());
        setShortDescription(null);
    }
}
