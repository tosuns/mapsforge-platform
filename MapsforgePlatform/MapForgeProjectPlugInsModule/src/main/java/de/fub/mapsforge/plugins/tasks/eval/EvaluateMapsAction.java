/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks.eval;

import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.models.Aggregator;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;

@ActionID(
        category = "Aggregator",
        id = "de.fub.mapsforge.plugins.tasks.eval.EvaluateMapsAction")
@ActionRegistration(lazy = false,
        displayName = "#CTL_EvaluateMapsAction")
//@ActionReference(path = "Loaders/text/aggregationbuilder+xml/Actions", position = 260)
@ActionReference(path = "Projects/Mapsforge/Detector/Tasks/MapRenderer/Actions", position = 3000)
@Messages("CTL_EvaluateMapsAction=Compare Maps")
public final class EvaluateMapsAction extends AbstractAction implements ContextAwareAction {

    private static final long serialVersionUID = 1L;
    private Lookup context;

    public EvaluateMapsAction() {
        super(Bundle.CTL_EvaluateMapsAction());
    }

    public EvaluateMapsAction(Lookup context) {
        this();
        this.context = context;
        validate();
    }

    private void validate() {
        if (context != null) {
            Collection<? extends Aggregator> allInstances = context.lookupResult(Aggregator.class).allInstances();
            setEnabled(allInstances.size() > 1);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (context != null) {
            Collection<? extends Aggregator> aggregatorList = context.lookupResult(Aggregator.class).allInstances();
            Collection<? extends Detector> detectorList = context.lookupResult(Detector.class).allInstances();
            if (!aggregatorList.isEmpty() && !detectorList.isEmpty()) {

                final SimpleMapsEvaluator evaluator = new SimpleMapsEvaluator(
                        MessageFormat.format("{0} [{1}]",
                        detectorList.iterator().next().getDetectorDescriptor().getName(),
                        Bundle.CLT_MapComparationTopComponent_Name()), aggregatorList);
                RequestProcessor.getDefault().post(new Runnable() {
                    @Override
                    public void run() {
                        evaluator.evaluate();
                    }
                });
            }
        }
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        return new EvaluateMapsAction(actionContext);
    }
}
