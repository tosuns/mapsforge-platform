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

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Main Window for the GPSk-application
 *
 * @author Christian Windolf
 */
public class MainWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private final OpenGPXAction openGPX = new OpenGPXAction();
    private final AboutAction aboutAction = new AboutAction();
    private final MapPanel mapPanel = new MapPanel();

    public MainWindow() {
        super("GPSk");
        setJMenuBar(new MenuBar());
        add(mapPanel);
        pack();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    protected class MenuBar extends JMenuBar {

        private static final long serialVersionUID = 1L;
        //File Menu Begin
        private final JMenu fileMenu = new JMenu("File");
        private final JMenuItem openFile = new JMenuItem(openGPX);
        //File Menu End
        //Help Menu Begin
        private final JMenu helpMenu = new JMenu("Help");
        private final JMenuItem aboutItem = new JMenuItem(aboutAction);
        //Help Menu End

        MenuBar() {
            super();

            add(fileMenu);
            fileMenu.add(openFile);

            add(helpMenu);
            helpMenu.add(aboutItem);
        }
    }

    public MapPanel getMapPanel() {
        return mapPanel;
    }
}
