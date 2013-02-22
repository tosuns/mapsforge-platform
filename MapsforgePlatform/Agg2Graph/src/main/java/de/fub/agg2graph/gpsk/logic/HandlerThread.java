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

import java.util.LinkedList;
import org.apache.log4j.Logger;

/**This class allows to handle GUI tasks independently from the GUI-thread
 * so it keeps responsive even on tasks requiring some CPU time.
 * 
 * The gui-thread prepares the task and gives it to this thread which takes care
 * of it.
 * 
 * In the end, this is just a non-preemptive FCFS-scheduler, so it does not 
 * garantee any kind of fairness, but probably this is not even necessary.
 * 
 * @author Christian Windolf
 */
public class HandlerThread extends Thread {
    private static Logger log = Logger.getLogger(HandlerThread.class);
    
    //just a list of handler, has not a real purpose...
    private final LinkedList<Task> handlerList = new LinkedList<Task>();

    //waiting queue for incoming tasks.
    private LinkedList<Task> taskList = new LinkedList<Task>();

    public HandlerThread() {
        super("Handler");
    }
    
    @Override
    public void run(){
        log.debug("started");
        while(true){
            Task t;
            synchronized(this){
                while(taskList.isEmpty()){
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        log.fatal("Handler has an interrupted exception :(");
                    }
                }
                t = taskList.removeFirst();
            }
            /*
             * if the task fails due a runtime exception and does not catch it,
             * we catch it here to keep the handler thread still running.
             */
            try{
                t.execute();
            } catch(Exception e){
                log.error("A task of type " + t.getClass().getName() + " crashed", e);
            }
        }
    }

    /**
     * Give a task to the handler-thread. It will take care of it.
     * @param t an object, that implements the {@link Task} interface.
     * Please be so kind and don't turn something over, that runs forever.
     */
    public void handleTask(Task t) {
        synchronized (this) {
            taskList.add(t);
            notify();
        }
    }
    
    public void addTask(Task h){
        handlerList.add(h);
    }
    
    
}
