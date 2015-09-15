package org.cifasis.mc1;

import com.google.common.collect.Sets;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by Cristian on 22/07/15.
 */
public class Algorithm1 {

    private final EventStructure es;                              /* the event structure to explore by the algorithm. */
    private final Set<EventStructure.Event> E;                    /* the set of events discovered by the algorithm. */

    /**
     * Construct a new instance of the exploration algorithm for a given event structure using the supplied continue
     * predicate.
     *
     * @param es
     * @param contPred
     */
    public Algorithm1(EventStructure es) {
        this.es = es;
        this.E = Sets.newTreeSet();
        E.add(es.getRoot());                   /* Add ⊥ to the set of discovered events */
    }

    /**
     * Search for alternative configurations that contain a given configuration and remain to be explored.
     *
     * @param C the configuration that must be contained by the alternative.
     * @param D the set of disabled events that must not appear in the alternative.
     * @return a valid alternative configuration, or empty set if not found.
     */
    private Set<EventStructure.Event> searchAlt(final Set<EventStructure.Event> C, final Set<EventStructure.Event> D) {
            if(D.stream().allMatch(new Predicate<EventStructure.Event>() {
                public boolean test(final EventStructure.Event eventD) {
                    return C.stream().anyMatch(new Predicate<EventStructure.Event>() {
                        public boolean test(EventStructure.Event eventC) {
                            return eventC.isInConflict(eventD);
                        }
                    });
                }
            })) {
                return C;
            }else {
                for (EventStructure.Event child : Sets.difference(es.getEnabled(C), D)) {
                    Set<EventStructure.Event> recCall = searchAlt(Sets.union(C, Sets.newHashSet(child)), D);
                    if (!recCall.isEmpty())
                        return recCall;
                }
            }
        return Sets.newHashSet();
    }

    /**
     * The exploration algorithm.
     *
     * @param C the initial configuration.
     * @param D a set of disabled events.
     * @param A a set of events to steer the exploration.
     */
    public void explore(final Set<EventStructure.Event> C, final Set<EventStructure.Event> D, Set<EventStructure.Event> A) {

        System.out.println(C + " | " + D + " | " + A);

        if (es.isMaximalConf(C))
            return;

        E.addAll(es.getExtensions(C));

        Set<EventStructure.Event> e;
        if (!A.isEmpty()) {
            e = Sets.newHashSet(Sets.intersection(es.getEnabled(C), A).iterator().next());
        } else {
            e = Sets.newHashSet(es.getEnabled(C).iterator().next());
        }

        explore(Sets.union(C, e), D, Sets.difference(A, e));

        Set<EventStructure.Event> altConf = searchAlt(C, Sets.union(D, e));
        if (!altConf.isEmpty()) {
            explore(C, Sets.union(D, e), Sets.difference(altConf, C));
        }
    }
}
