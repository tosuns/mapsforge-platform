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
package de.fub.maps.project.actions;

import de.fub.maps.project.MapsProject;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Project",
        id = "de.fub.maps.project.actions.CloseProjectAction")
@ActionRegistration(
        displayName = "#CTL_CloseProjectAction")
@ActionReference(id
        = @ActionID(
                category = "Project",
                id = "de.fub.maps.project.actions.CloseProjectAction"),
        path = "Loaders/text/mapsproject+xml/Actions",
        position = 900, separatorAfter = 1000)
@Messages("CTL_CloseProjectAction=Close")
public final class CloseProjectAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final Action delegateCloseAction = CommonProjectActions.closeProjectAction();
    private transient final MapsProject context;

    public CloseProjectAction(Project context) {
        super();
        super.putValue(NAME, delegateCloseAction.getValue(NAME));
        if (context instanceof MapsProject) {
            this.context = (MapsProject) context;
            setEnabled(true);
        } else {
            this.context = null;
            setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        delegateCloseAction.actionPerformed(e);
    }
}
