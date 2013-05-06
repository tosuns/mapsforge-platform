/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks.eval;

import de.fub.mapsforge.project.models.Aggregator;
import java.awt.event.ActionEvent;
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
@ActionRegistration(lazy = true,
        displayName = "#CTL_EvaluateMapsAction")
//@ActionReference(path = "Loaders/text/aggregationbuilder+xml/Actions", position = 260)
@ActionReference(path = "Projects/Mapsforge/Detector/Tasks/MapRenderer/Actions", position = 3000)
@Messages("CTL_EvaluateMapsAction=Compare Maps")
public final class EvaluateMapsAction extends AbstractAction implements ContextAwareAction {

    private static final long serialVersionUID = 1L;
    private final Lookup context;

    public EvaluateMapsAction(Lookup context) {
        this.context = context;
        validate();
    }

    private void validate() {
        Collection<? extends Aggregator> allInstances = context.lookupResult(Aggregator.class).allInstances();
        setEnabled(allInstances.size() > 1);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        Collection<? extends Aggregator> instanceList = context.lookupResult(Aggregator.class).allInstances();
        final SimpleMapsEvaluator evaluator = new SimpleMapsEvaluator(instanceList);
        RequestProcessor.getDefault().post(new Runnable() {
            @Override
            public void run() {
                evaluator.evaluate();
            }
        });
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        return new EvaluateMapsAction(actionContext);
    }
}
