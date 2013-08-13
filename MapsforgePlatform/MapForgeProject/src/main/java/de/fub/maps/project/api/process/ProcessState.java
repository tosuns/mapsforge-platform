/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.api.process;

/**
 *
 * @author Serdar
 */
public enum ProcessState {
    // settings error occurs when the process has a configuration error and
    // causes the process unit not to work propertly. a process unit should in this case
    // not work/proceed

    SETTING_ERROR("Setting Error"),
    // an error occurred and the process unit is interrupted / stoped during its run.
    // A process unit still can be restarted in this case.
    ERROR("Error"),
    // indicate the process unit is running.
    RUNNING("Running"),
    // the default state of a process unit. the process unit doesn't work, is
    // inactive.
    INACTIVE("Inactive");
    private String displayName;

    private ProcessState(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
