package org.cifasis.mc1;

import com.google.common.collect.Sets;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by cristian on 29/07/15.
 */
public class Algorithm2 {

    private final EventStructure es;                              /* the event structure to explore by the algorithm. */
    private final Set<EventStructure.Event> E;                    /* the set of events discovered by the algorithm. */
    private Set<EventStructure.Event> BE;                   /* the events discovered but suspended due the bound */
    private final Predicate<Set<EventStructure.Event>> contPred;  /* a predicate to decide the backtracking. */
    private final int lfsBound;

    /**
     * Construct a new instance of the exploration algorithm for a given event structure using the supplied lfs bound.
     *
     * @param es
     * @param lfsBound
     */
    public Algorithm2(EventStructure es, int lfsBound) {
        this.es = es;
        this.contPred = LFS_BOUND;
        this.E = Sets.newHashSet(es.getRoot());                   /* Add ⊥ to the set of discovered events */
        this.BE = Sets.newTreeSet();
        this.lfsBound = lfsBound;
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
     * This predicate enforces a LFS-bound of max(C) <= lfsBound.
     */
    public final Predicate<Set<EventStructure.Event>> LFS_BOUND = new Predicate<Set<EventStructure.Event>>() {
        public boolean test(Set<EventStructure.Event> conf) {
//            System.out.println("Continue?: " + conf + " , maximal: " + EventStructure.getMaximalEvents(conf));
            return EventStructure.getMaximalEvents(conf).size() <= lfsBound;
        }
    };

    /**
     * Search for alternative configurations that contain a given configuration and remain to be explored.
     *
     * @param C the configuration that must be contained by the alternative.
     * @param D the set of disabled events that must not appear in the alternative.
     * @return a valid alternative configuration, or empty set if not found.
     */
    private Set<EventStructure.Event> searchAlt(final Set<EventStructure.Event> C, final Set<EventStructure.Event> D) {
        for (final EventStructure.Event child : es.getEnabled(C)) {
            if (contPred.test(Sets.union(C, Sets.newHashSet(child)))) {
                if (D.stream().allMatch(new Predicate<EventStructure.Event>() {
                    public boolean test(final EventStructure.Event eventD) {
                        return Sets.union(C, Sets.newHashSet(child)).stream().anyMatch(new Predicate<EventStructure.Event>() {
                            public boolean test(EventStructure.Event eventC) {
                                return eventC.isInConflict(eventD);
                            }
                        });
                    }
                }) || (!D.contains(child) && BE.contains(child))) {
                    return Sets.union(C, Sets.newHashSet(child));
                }
            }
        }
        return Sets.newHashSet();
    }

    private Set<EventStructure.Event> getEnabledInBound(final Set<EventStructure.Event> conf) {
        Set<EventStructure.Event> enabledEvents = es.getEnabled(conf);
        return enabledEvents.stream().filter(new Predicate<EventStructure.Event>() {
            public boolean test(EventStructure.Event event) {
                return contPred.test(Sets.union(conf, Sets.newHashSet(event)));
            }
        }).collect(Collectors.<EventStructure.Event>toSet());
    }

    private Set<EventStructure.Event> getBounded(final Set<EventStructure.Event> conf) {
        Set<EventStructure.Event> enabledEvents = es.getEnabled(conf);
        return enabledEvents.stream().filter(new Predicate<EventStructure.Event>() {
            public boolean test(EventStructure.Event event) {
                return !contPred.test(Sets.union(conf, Sets.newHashSet(event)));
            }
        }).collect(Collectors.<EventStructure.Event>toSet());
    }

    /**
     * The exploration algorithm.
     *
     * @param C the initial configuration.
     * @param D a set of disabled events.
     * @param A a set of events to steer the exploration.
     */

    public void explore(final Set<EventStructure.Event> C, final Set<EventStructure.Event> D, Set<EventStructure.Event> A) {

        System.out.println(C + " | " + D + " | " + A + " ~~ " + BE);

        BE = Sets.difference(getBounded(C), D);

        // Check if it is a maximal configuration for the bound.
        if (Sets.difference(getEnabledInBound(C), D).isEmpty())
            return;

        E.addAll(es.getExtensions(C));

        Set<EventStructure.Event> e;
        if (A.isEmpty()) {
            e = Sets.newHashSet(Sets.difference(getEnabledInBound(C), D).iterator().next());
        } else {
            e = Sets.newHashSet(Sets.difference(Sets.intersection(es.getEnabled(C), A), D).iterator().next());
        }

        explore(Sets.union(C, e), D, Sets.difference(A, e));

        Set<EventStructure.Event> altConf = searchAlt(C, Sets.union(D, e));
        if (!altConf.isEmpty()) {

            explore(C, Sets.union(D, e), Sets.newHashSet(Sets.difference(altConf, C).iterator().next()));
        }

    }
}
