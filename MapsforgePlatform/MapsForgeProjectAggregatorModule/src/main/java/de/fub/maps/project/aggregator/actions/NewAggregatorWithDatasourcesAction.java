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

import de.fub.gpxmodule.GPXDataObject;
import de.fub.maps.project.aggregator.actions.wizards.aggregator.AggregatorWizardWithDatasourcesAction;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;

@ActionID(
        category = "GPX",
        id = "de.fub.maps.project.aggregator.actions.NewAggregatorWithDatasourcesAction")
@ActionRegistration(
        //    iconBase = "de/fub/mapsforge/project/aggregator/filetype/aggregationBuilderIcon.png",
        displayName = "#CTL_CreateAggregationBuilderAction", lazy = false)
@ActionReferences({
    //    @ActionReference(path = "Menu/GPX", position = 5),
    @ActionReference(path = "Toolbars/GPX", position = 5),
    @ActionReference(path = "Loaders/text/gpx+xml/Actions", position = 215)
})
@Messages("CTL_CreateAggregationBuilderAction=New Aggregator")
public final class NewAggregatorWithDatasourcesAction extends AbstractAction implements ContextAwareAction, LookupListener {

    @StaticResource
    private static final String ICON_PATH = "de/fub/maps/project/aggregator/filetype/aggregationBuilderIcon.png";
    private static final long serialVersionUID = 1L;
    private transient final Lookup.Result<GPXDataObject> context;

    public NewAggregatorWithDatasourcesAction() {
        this(Utilities.actionsGlobalContext());
    }

    public NewAggregatorWithDatasourcesAction(Lookup lookup) {
        super(Bundle.CTL_CreateAggregationBuilderAction());
        putValue(Action.SMALL_ICON, ImageUtilities.loadImageIcon(ICON_PATH, false));
        putValue("iconBase", ICON_PATH);
        context = lookup.lookupResult(GPXDataObject.class);
        context.addLookupListener(NewAggregatorWithDatasourcesAction.this);
        resultChanged(new LookupEvent(context));
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        new AggregatorWizardWithDatasourcesAction(context.allInstances()).actionPerformed(ev);
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        return new NewAggregatorWithDatasourcesAction(actionContext);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setEnabled(context.allInstances().size() > 0);
            }
        });
    }
}
