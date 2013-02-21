/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.actions;

import de.fub.gpxmodule.GPXDataObject;
import de.fub.mapsforge.project.aggregator.actions.wizards.aggregator.AggregatorWizardWithDatasourcesAction;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
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
        id = "de.fub.mapsforge.project.aggregator.actions.NewAggregatorWithDatasourcesAction")
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
    private static final String ICON_PATH = "de/fub/mapsforge/project/aggregator/filetype/aggregationBuilderIcon.png";
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
        setEnabled(context.allInstances().size() > 0);
    }
}
