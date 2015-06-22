package org.cifasis.mc1;

import java.util.HashSet;
import java.util.Set;

/**
 * The scheduler is a singletone class that drives the exploration.
 * Created by Cristian on 22/06/15.
 */
public class Scheduler extends MCProcess {

    private final Set<MCProcess> processSet;    // the set of processes in the system

    public Scheduler() {
        super("Scheduler");
        this.processSet = new HashSet<MCProcess>();
    }

    /**
     * Add a new process to the exploration.
     * @param name  the name of the process.
     */
    public void addProcess(MCProcess newProcess) {
        this.processSet.add(newProcess);
    }

    /**
     * Main loop of the scheduler
     */
    public void run() {
        for(MCProcess process: processSet){
            process.resume();
            this.suspend();
        }
    }
}
