/*
 * Copyright (C) 2013 Christian Windolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fub.agg2graph.gpsk.logic;

import de.fub.agg2graph.gpsk.Main;
import de.fub.agg2graph.input.GPXReader;
import de.fub.agg2graph.structs.GPSTrack;
import java.io.File;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import org.apache.log4j.Logger;

/**
 *
 * @author Christian Windolf
 */
public class OpenFileHandler implements Observer, Task {

    private static Logger log = Logger.getLogger(OpenFileHandler.class);
    private File f;

    public OpenFileHandler() {
        Main.openedFile.addObserver(this);
        log.debug("added myself as observer");
    }

    @Override
    public void update(Observable o, Object arg) {
        log.debug("notified about a new file");
        if (arg instanceof File) {
            f = (File) arg;
            Main.handler.handleTask(this);
        } else {
            throw new IllegalArgumentException("The OpenFileHandler can only handle files!");
        }

    }

    @Override
    public void execute() {
        if (!f.exists()) {
            showMessageDialog(Main.mainWindow, "Sorry, but the file "
                    + f.getAbsolutePath() + " does not exist!",
                    "I/O Error", ERROR_MESSAGE);
            return;
        }
        try {
            List<GPSTrack> tracks = GPXReader.getTracks(f);
            Main.mainWindow.getMapPanel().setMap(tracks);
        } catch (Exception e) {
            log.error("unable to open " + f.getName(), e);
        }
    }
}
