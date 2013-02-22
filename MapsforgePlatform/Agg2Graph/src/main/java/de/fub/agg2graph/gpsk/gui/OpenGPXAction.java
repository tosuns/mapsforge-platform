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
package de.fub.agg2graph.gpsk.gui;

import de.fub.agg2graph.gpsk.Main;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.UIManager;

import static java.awt.event.ActionEvent.CTRL_MASK;
import static java.awt.event.KeyEvent.VK_O;
import java.io.File;
import javax.swing.JFileChooser;
import static javax.swing.KeyStroke.getKeyStroke;
import javax.swing.filechooser.FileFilter;


import static javax.swing.JFileChooser.APPROVE_OPTION;

/**
 *
 * @author Christian Windolf
 */
public class OpenGPXAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private JFileChooser fChooser = new JFileChooser();
    private static final String desc = "opens a GPX-file";

    public OpenGPXAction() {
        super("open", UIManager.getIcon("Tree.openIcon"));
        putValue(ACCELERATOR_KEY, getKeyStroke(VK_O, CTRL_MASK));
        putValue(SHORT_DESCRIPTION, desc);
        fChooser.setFileFilter(new GPXFilter());
        fChooser.addChoosableFileFilter(new AcceptAll());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int result = fChooser.showOpenDialog(Main.mainWindow);
        if (result == APPROVE_OPTION) {
            Main.openedFile.setFile(fChooser.getSelectedFile());
        }
    }

    private class GPXFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            if (f.getName().toLowerCase().endsWith(".gpx")) {
                return true;
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "GPX-Files (*.gpx)";
        }
    }

    private class AcceptAll extends FileFilter {

        @Override
        public boolean accept(File f) {
            return true;
        }

        @Override
        public String getDescription() {
            return "All Files (*.*)";
        }
    }
}
