package org.cifasis.mc1;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.util.Optional;
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
    private int traceCount;                                       /* The number of explored traces */
    private int traceSizeSum;                                     /* The sum of the sizes of all maximal traces */

    /**
     * Construct a new instance of the exploration algorithm for a given event structure using the supplied m and n
     * parameters.
     *
     * @param es the event structure
     * @param m  maximum parallel degree of the event structure
     * @param n  communication degree of the event structure
     */
    public Algorithm2(EventStructure es, int m, int n) {
        this.es = es;
        this.contPred = LFS_BOUND;
        this.E = Sets.newHashSet(es.getRoot());                   /* Add ⊥ to the set of discovered events */
        this.V = Sets.newHashSet(es.getRoot());
        this.lfsBound = computeBound(m, n);
        this.traceCount = 0;
    }

    /**
     * Construct a new instance of the exploration algorithm for a given event structure using the supplied bound.
     *
     * @param es       the event structure
     * @param lfsBound LFS-number to use as bound
     */
    public Algorithm2(EventStructure es, int lfsBound) {
        this.es = es;
        this.contPred = LFS_BOUND;
        this.E = Sets.newHashSet(es.getRoot());                   /* Add ⊥ to the set of discovered events */
        this.V = Sets.newHashSet(es.getRoot());
        this.lfsBound = lfsBound;
        this.traceCount = 0;
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

    public Set<EventStructure.Event> getV() {
        return V;
    }

    public int getTraceCount() {
        return this.traceCount;
    }

    public int getTraceSizeAvg() {
        return traceSizeSum / traceCount;
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
            if (D.stream().allMatch(new Predicate<EventStructure.Event>() {
                public boolean test(final EventStructure.Event eventD) {
                    return newC.stream().anyMatch(new Predicate<EventStructure.Event>() {
                        public boolean test(EventStructure.Event eventC) {
                            return eventC.isInConflict(eventD);
                        }
                    });
                }
            }) && (!Sets.difference(newC, V).isEmpty() || contPred.test(newC))) {
                return newC;

            } else {
                return searchAlt(newC, D);
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

        System.out.println("C=" + C + " | D=" + D + " | A=" + A + " | en=" + es.getEnabled(C) + " | E=" + E);

        E.addAll(es.getExtensions(C));

        if (es.isMaximalConf(C)) {
            traceSizeSum += C.size();
            traceCount++;
            System.out.println();
            return;
        }

        Set<EventStructure.Event> eSet;
        if (!A.isEmpty()) {
            eSet = ImmutableSet.copyOf(Iterables.limit(Sets.intersection(A, es.getEnabled(C)), 1));
        } else {
            Optional<EventStructure.Event> e = es.getEnabled(C).stream().filter(event -> !V.contains(event) || contPred.test(Sets.union(C, Sets.newHashSet(event)))).findFirst();
            if (e.isPresent())
                eSet = ImmutableSet.of(e.get());
            else {
                traceSizeSum += C.size();
                traceCount++;
                System.out.println();
                return;
            }
        }

        Set<EventStructure.Event> newC = Sets.union(C, eSet);

        V.add(eSet.iterator().next());

        explore(newC, D, Sets.difference(A, eSet));

        Set<EventStructure.Event> altConf = Sets.difference(searchAlt(C, Sets.union(D, eSet)), C);
        if (!altConf.isEmpty()) {
            //if (Sets.intersection(altConf, V).isEmpty() || contPred.test(Sets.union(C, altConf)))
            explore(C, Sets.union(D, eSet), altConf);
        }
    }

    public static int computeBound(int m, int n) {
        return ((int) ((n - 1) * (Math.log(m) / Math.log(n))) + 1);
    }
}
