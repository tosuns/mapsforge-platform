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
    private final String displayName;

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
