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
package de.fub.maps.project.plugins.tasks.map;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Detector",
        id = "de.fub.maps.project.plugins.tasks.map.ClearMapRendererAction")
@ActionRegistration(
        displayName = "#CTL_ClearMapRendererAction")
@ActionReference(path = "Project/Maps/Plugin/Maprenderer/Actions")
@Messages("CTL_ClearMapRendererAction=Clear")
public final class ClearMapRendererAction implements ActionListener {

    private final MapRenderer context;

    public ClearMapRendererAction(MapRenderer context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        context.clear();
    }
}
