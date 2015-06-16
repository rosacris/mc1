package org.cifasis.mc1;

import com.google.common.base.Objects;

import java.util.HashMap;
import java.util.Map;

/**
 * A model-checker state object represents a mapping between variable names and their values.
 * Created by Cristian on 15/06/15.
 */
public class MCState {

    private final HashMap<String, Int> state = new HashMap<String, Int>();    // The state is a set of vars

    /**
     * Add a new var to the state object
     * @param name  the name of the new variable
     * @param val   the value of the new variable
     * @return      an instance of the new variable
     */
    public Int newVar(String name, int val) {
        Int newVar = new Int(val);
        state.put(name, newVar);
        return newVar;
    }

    /**
     * Create a copy of a MCState instance
     * @param that  the MCState instance to copy
     * @return      a new MCState instance
     */
    public static MCState copyOf(MCState that) {
        MCState newMCState = new MCState();
        for(Map.Entry<String, Int> var : that.state.entrySet()){
            newMCState.newVar(var.getKey(), var.getValue().intValue());
        }
        return newMCState;
    }

    /**
     * Get a variable from the state instance
     * @param name  the name of the variable
     * @return      the value of the variable
     */
    public Int getVar(String name) {
        return state.get(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MCState)) return false;
        MCState mcState1 = (MCState) o;
        return Objects.equal(state, mcState1.state);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(state);
    }

    @Override
    public String toString() {
        return "" + state;
    }
}
