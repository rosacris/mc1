package org.cifasis.mc1;

/**
 * A model-checker process is the unit of execution in the system.
 * It represents an asynchronous entity with local state.
 * Note that we use the word process to denote state isolation, however the implementation is based on threads.
 * Created by Cristian on 15/06/15.
 */
public abstract class MCProcess implements Runnable {
    private Thread t;                                       // the thread that runs the process
    private final String name;                              // the name of the process
    private boolean suspended = false;                      // a flag to control the execution of the process
    protected final MCState localMCState = new MCState();   // the local state of the process

    /**
     * Create a new model-checker process.
     * @param name  the name of the process
     */
    public MCProcess(String name) {
        this.name = name;
    }

    /**
     * Get a copy of the process state.
     * @return  a model-checker state object
     */
    public final MCState getMCState() {
        return MCState.copyOf(localMCState);
    }

    /**
     * Start the execution of the process.
     */
    public final void start() {
        if (t == null) {
            t = new Thread(this, name);
            t.start();
        }
    }

    /**
     * Suspend the execution of the process.
     * Note that it can only be suspended from the process itself.
     */
    synchronized protected final void suspend() {
        try {
            suspended = true;
            while (suspended) {
                wait();
            }
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }

    /**
     * Resume the execution of the process.
     */
    synchronized public final void resume() {
        suspended = false;
        notify();
    }
}
