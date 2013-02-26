/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapforgeproject.api.process;

/**
 *
 * @author Serdar
 */
public enum ProcessState {

    ERROR("Error"),
    RUNNING("Running"),
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
