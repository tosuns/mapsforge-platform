/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.factories.nodes;

import de.fub.maps.project.detector.model.xmls.ProcessDescriptor;
import de.fub.utilsmodule.node.CustomAbstractnode;
import de.fub.utilsmodule.node.property.ProcessProperty;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
public class ProcessDescriptionNode extends CustomAbstractnode {

    private final ProcessDescriptor processDescriptor;
    private final ModelSynchronizer.ModelSynchronizerClient msClient;

    public ProcessDescriptionNode(ModelSynchronizer.ModelSynchronizerClient msClient, ProcessDescriptor processDesciptor) {
        super(Children.LEAF, Lookups.fixed(processDesciptor));
        this.processDescriptor = processDesciptor;
        this.msClient = msClient;
        setDisplayName(processDesciptor.getName());
        setShortDescription(processDesciptor.getDescription());
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        sheet.put(set);

        ProcessProperty processProperty = null;
        for (de.fub.maps.project.detector.model.xmls.Property property : processDescriptor.getProperties().getPropertyList()) {
            processProperty = new ProcessProperty(msClient, property);
            set.put(processProperty);
        }
        return sheet;
    }
}
