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
package de.fub.agg2graph.gpsk;

import de.fub.agg2graph.gpsk.gui.MainWindow;
import de.fub.agg2graph.gpsk.logic.HandlerThread;
import de.fub.agg2graph.gpsk.logic.OpenFileHandler;
import de.fub.agg2graph.gpsk.obs.OpenedFile;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Main Class that contains the main-method for the executable
 * gpsk-jar file. It also provides global variables for the application.
 *
 * @author Christian Windolf
 */
public class Main {
    
    private static Logger log = Logger.getLogger(Main.class);
    private static final PatternLayout layout = 
            new PatternLayout("[%p] \u001B[34m[%t] %C{1}:\u001B[0m %m \t at %d%n");
    
    public static final String version = "0.1 SNAPSHOT";
    
    public static MainWindow mainWindow = null;
    
    public static HandlerThread handler;
    
    public static OpenedFile openedFile = null;
    
    public static void main(String[] args){
        //setting up log4j...
        Logger.getRootLogger().addAppender(new ConsoleAppender(layout));
        Logger.getRootLogger().setLevel(Level.DEBUG);
        log.info("GPSk started - Version: " + version);
        
        mainWindow = new MainWindow();
        openedFile = new OpenedFile();
        handler = new HandlerThread();
        handler.addTask(new OpenFileHandler());
        
        
        handler.start();
    }
    
}
