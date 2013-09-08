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
package de.fub.maps.project.plugins.tasks.eval;

import de.fub.maps.project.detector.model.Detector;
import de.fub.maps.project.models.Aggregator;
import de.fub.maps.project.plugins.tasks.eval.evaluator.SimpleMapsEvaluator;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;

@ActionID(
        category = "Aggregator",
        id = "de.fub.maps.project.plugins.tasks.eval.EvaluateMapsAction")
@ActionRegistration(lazy = false,
        displayName = "#CTL_EvaluateMapsAction")
//@ActionReference(path = "Loaders/text/aggregationbuilder+xml/Actions", position = 260)
@ActionReferences({
    @ActionReference(
            id
            = @ActionID(
                    category = "Aggregator",
                    id = "de.fub.maps.project.plugins.tasks.eval.EvaluateMapsAction"),
            path = "Loaders/text/aggregationbuilder+xml/Actions", position = 280),
    @ActionReference(
            id
            = @ActionID(
                    category = "Aggregator",
                    id = "de.fub.maps.project.plugins.tasks.eval.EvaluateMapsAction"),
            path = "Projects/Maps/Detector/Tasks/MapRenderer/Actions", position = 3000)
})
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
