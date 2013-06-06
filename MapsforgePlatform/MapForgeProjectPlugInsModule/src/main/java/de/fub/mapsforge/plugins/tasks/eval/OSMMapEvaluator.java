/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks.eval;

import de.fub.agg2graph.roadgen.RoadNetwork;
import de.fub.mapsforge.plugins.mapmatcher.MapMatcher;
import de.fub.mapsforgeplatform.openstreetmap.service.MapProvider;
import java.awt.Dialog;
import javax.swing.SwingUtilities;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;

/**
 *
 * @author Serdar
 */
public class OSMMapEvaluator {

    private final RoadNetwork roadNetwork;

    public OSMMapEvaluator(RoadNetwork roadNetwork) {
        assert roadNetwork != null;
        this.roadNetwork = roadNetwork;
    }

    public void evaluate() {
        final EvaluationOptionPanel evaluationOptionPanel = new EvaluationOptionPanel();

        DialogDescriptor descriptor = new DialogDescriptor(evaluationOptionPanel, "Evaluator Option Dialog", true, evaluationOptionPanel.getButtons(), evaluationOptionPanel.getOkButton(), DialogDescriptor.RIGHT_ALIGN, HelpCtx.DEFAULT_HELP, null);
        Dialog dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        dialog.setVisible(true);
        if (descriptor.getValue() == evaluationOptionPanel.getOkButton()) {
            final MapMatcher mapMatcher = evaluationOptionPanel.getMapMatcher();
            final MapProvider mapProvider = evaluationOptionPanel.getMapProvider();

            if (mapMatcher != null && mapProvider != null) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        EvaluatorTopComponent evaluatorTopComponent = new EvaluatorTopComponent(mapMatcher, mapProvider, roadNetwork);
                        evaluatorTopComponent.open();
                        evaluatorTopComponent.requestVisible();
                    }
                });

            } else {
                NotifyDescriptor.Message nd = new NotifyDescriptor.Message("MapMatcher or MapProvider instance are null. This state is not permitted!");
                DialogDisplayer.getDefault().notifyLater(nd);
            }
        }
    }
}
