/*
 * Copyright (C) 2013 Serdar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
