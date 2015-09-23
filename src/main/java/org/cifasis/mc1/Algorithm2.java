package org.cifasis.mc1;

import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by cristian on 29/07/15.
 */
public class Algorithm2 {

    private final EventStructure es;                              /* the event structure to explore by the algorithm. */
    private final Set<EventStructure.Event> E;                    /* the set of events discovered by the algorithm. */
    private Set<EventStructure.Event> V;                          /* the set of visited events */
    private final Predicate<Set<EventStructure.Event>> contPred;  /* a predicate to decide the backtracking. */
    private final int lfsBound;

    /**
     * Construct a new instance of the exploration algorithm for a given event structure using the supplied m and n
     * parameters.
     *
     * @param m        maximum parallel degree of the event structure
     * @param n        communication degree of the event structure
     * @param es
     * @param lfsBound
     */
    public Algorithm2(EventStructure es, int m, int n) {
        this.es = es;
        this.contPred = LFS_BOUND;
        this.E = Sets.newHashSet(es.getRoot());                   /* Add ⊥ to the set of discovered events */
        this.V = Sets.newHashSet(es.getRoot());
        this.lfsBound = computeBound(m, n);
    }

    /**
     * Construct a new instance of the exploration algorithm for a given event structure using the supplied bound.
     *
     * @param lfsBound LFS-number to use as bound
     * @param es
     * @param lfsBound
     */
    public Algorithm2(EventStructure es, int lfsBound) {
        this.es = es;
        this.contPred = LFS_BOUND;
        this.E = Sets.newHashSet(es.getRoot());                   /* Add ⊥ to the set of discovered events */
        this.V = Sets.newHashSet(es.getRoot());
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

    public Set<EventStructure.Event> getE() {
        return E;
    }

    public Set<EventStructure.Event> getV(){
        return V;
    }

    /**
     * Search for alternative configurations that contain a given configuration and remain to be explored.
     *
     * @param C the configuration that must be contained by the alternative.
     * @param D the set of disabled events that must not appear in the alternative.
     * @return a valid alternative configuration, or empty set if not found.
     */
    private Set<EventStructure.Event> searchAlt(final Set<EventStructure.Event> C, final Set<EventStructure.Event> D) {
        for (final EventStructure.Event event : Sets.difference(Sets.intersection(es.getEnabled(C), E), D)) {
            Set<EventStructure.Event> newC = Sets.union(C, Sets.newHashSet(event));
            if (contPred.test(newC)) {
                Set<EventStructure.Event> altConf = searchAlt(newC, D);
                if (!Sets.difference(altConf, V).isEmpty()) {
                    return altConf;
                }
            }
        }
        return C;
    }

    /**
     * The exploration algorithm.
     *
     * @param C the initial configuration.
     * @param D a set of disabled events.
     * @param A a set of events to steer the exploration.
     */

    public void explore(final Set<EventStructure.Event> C, final Set<EventStructure.Event> D, Set<EventStructure.Event> A) {
        E.addAll(es.getExtensions(C));

        if (!contPred.test(C)) {
            System.out.println();
            return;
        }

        System.out.println("C=" + C + " | D=" + D + " | A=" + A + " | en=" + es.getEnabled(C) + " | E=" + E);

        V.addAll(C);

        Set<EventStructure.Event> enC = es.getEnabled(C);

        if (Sets.difference(enC, D).isEmpty()) {
            System.out.println();
            return;
        }

        EventStructure.Event e;
        if (!A.isEmpty()) {
            e = Sets.intersection(enC, A).stream().sorted((e1, e2) -> Integer.compare(EventStructure.getEventDetph(C, e2), EventStructure.getEventDetph(C, e1))).findFirst().get();
        } else {
            e = Sets.difference(enC, D).stream().sorted((e1, e2) -> Integer.compare(EventStructure.getEventDetph(C, e2), EventStructure.getEventDetph(C, e1))).findFirst().get();
        }

        Set<EventStructure.Event> eSet = Collections.singleton(e);
        explore(Sets.union(C, eSet), D, Sets.difference(A, eSet));

        Set<EventStructure.Event> altConf = searchAlt(C, Sets.union(D, eSet));
        if (!Sets.difference(altConf, C).isEmpty()) {
            explore(C, Sets.union(D, eSet), Sets.difference(altConf, C));
        }

        if (E.equals(V)) {
            System.out.println("ES events count: " + es.getEventSet().size());
            System.out.println("Visited events count: " + this.getV().size());
            throw new RuntimeException();
        }
    }

    public static int computeBound(int m, int n) {
        return ((int) ((n - 1) * (Math.log(m) / Math.log(n))) + 1);
    }
}
