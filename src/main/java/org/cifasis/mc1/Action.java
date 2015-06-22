package org.cifasis.mc1;

/**
 * An action represents the visible actions of the processes in the shared state.
 * Created by Cristian on 22/06/15.
 */
public class Action {

    private final String name;
    private final MCProcess process;

    public Action(String name, MCProcess process) {
        this.name = name;
        this.process = process;
    }

    public String getName() {
        return name;
    }

    public MCProcess getProcess() {
        return process;
    }
}
