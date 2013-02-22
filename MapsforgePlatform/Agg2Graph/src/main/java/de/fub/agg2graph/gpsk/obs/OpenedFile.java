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
package de.fub.agg2graph.gpsk.obs;

import java.io.File;
import java.util.Observable;

/**
 *
 * @author Christian Windolf
 */
public class OpenedFile extends Observable{
    
    private File file = null;
    
    public void setFile(File f){
        this.file = f;
        setChanged();
        notifyObservers(f);
    }
    
    public File getFile(){
        return file;
    }

}
