/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.gpxmodule.actions;

import de.fub.gpxmodule.actions.ui.StatisticForm;
import de.fub.gpxmodule.xml.Trkseg;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "GPX",
        id = "de.fub.gpxmodule.actions.StatisticsAction")
@ActionRegistration(
        displayName = "#CTL_StatisticsAction")
@Messages("CTL_StatisticsAction=Statistics")
public final class StatisticsAction implements ActionListener {

    private final Trkseg context;

    public StatisticsAction(Trkseg context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        StatisticForm statisticForm = new StatisticForm(context);
        DialogDescriptor dd = new DialogDescriptor(statisticForm, "Statistic Dialog");
        DialogDisplayer.getDefault().createDialog(dd).setVisible(true);
    }
}
