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

/**This is only some kind of a marker interface for tasks that should run
 * in another thread. 
 * If you got a task that should run in another thread than the gui thread,
 * implement this interface. AND PLEASE: try not crash, the whole handler 
 * thread will do so, too...
 *
 * 
 * @author Christian Windolf
 */
public interface Task {
    
    /**
     * everything you need to know is already written in the interface 
     * description itself.
     */
    public void execute();
    
}
