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
    private final Predicate<Set<EventStructure.Event>> contPred;  /* a predicate to decide the backtracking. */

    /**
     * Construct a new instance of the exploration algorithm for a given event structure using the supplied continue
     * predicate.
     *
     * @param es
     * @param contPred
     */
    public Algorithm1(EventStructure es, Predicate<Set<EventStructure.Event>> contPred) {
        this.es = es;
        this.contPred = contPred;
        this.E = Sets.newHashSet(es.getRoot());                   /* Add ⊥ to the set of discovered events */
    }

    /**
     * This predicate always continue.
     */
    public static final Predicate<Set<EventStructure.Event>> ALWAYS_CONTINUE = new Predicate<Set<EventStructure.Event>>() {
        public boolean test(Set<EventStructure.Event> conf) {
            return true;
        }
    };

    /**
     * This predicate enforces a LFS-bound of max(C) <= 1.
     */
    public static final Predicate<Set<EventStructure.Event>> LFS_BOUND = new Predicate<Set<EventStructure.Event>>() {
        public boolean test(Set<EventStructure.Event> conf) {
            System.out.println("Continue?: " + conf + " , maximal: " + EventStructure.getMaximalEvents(conf));
            return EventStructure.getMaximalEvents(conf).size() <= 1;
        }
    };

    /**
     * Search for alternative configurations that contain a given configuration and remain to be explored.
     *
     * @param E the search space.
     * @param C the configuration that must be contained by the alternative.
     * @param D the set of disabled events that must not appear in the alternative.
     * @return a valid alternative configuration, or empty set if not found.
     */
    private static Set<EventStructure.Event> searchAlt(Set<EventStructure.Event> E, Set<EventStructure.Event> C, Set<EventStructure.Event> D) {
        for (EventStructure.Event e : E) {
            final Set<EventStructure.Event> altConf = Sets.union(C, Sets.newHashSet(e));
            if (EventStructure.isConf(altConf)) {
                if (D.stream().allMatch(new Predicate<EventStructure.Event>() {
                    public boolean test(final EventStructure.Event event) {
                        return altConf.stream().anyMatch(new Predicate<EventStructure.Event>() {
                            public boolean test(EventStructure.Event that) {
                                return that.isInDirectConflict(event);
                            }
                        });
                    }
                })) {
                    return altConf;
                }
            }
        }
        return Sets.newTreeSet();
    }

    /**
     * The exploration algorithm.
     *
     * @param C the initial configuration.
     * @param D a set of disabled events.
     * @param A a set of events to steer the exploration.
     */
    public void explore(Set<EventStructure.Event> C, Set<EventStructure.Event> D, Set<EventStructure.Event> A) {

        System.out.println(C + " | " + D + " | " + A + " ~~ " + Sets.difference(C, es.getEventSet()));

        if (es.isMaximalConf(C))
            return;

        E.addAll(es.getExtensions(C));

        Set<EventStructure.Event> e;
        if (!A.isEmpty())
            e = Sets.newHashSet(Sets.intersection(es.getEnabled(C), A).iterator().next());
        else
            e = Sets.newHashSet(es.getEnabled(C).iterator().next());

        if (contPred.test(Sets.union(C, e)))
            explore(Sets.union(C, e), D, Sets.difference(A, e));

        Set<EventStructure.Event> altConf = searchAlt(E, C, Sets.union(D, e));
        if (!altConf.isEmpty() && contPred.test(altConf)) {
            explore(C, Sets.union(D, e), Sets.difference(altConf, C));
        }
    }
}
