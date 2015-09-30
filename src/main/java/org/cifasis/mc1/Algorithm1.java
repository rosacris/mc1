package org.cifasis.mc1;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by Cristian on 22/07/15.
 */
public class Algorithm1 {

    private final EventStructure es;                              /* the event structure to explore by the algorithm. */
    private final Set<EventStructure.Event> E;                    /* the set of events discovered by the algorithm. */
    private int lfsNumber = 0;
    private int numThreads = 0;
    private int traceCount;                                       /* The number of explored traces */
    private int traceSizeSum;                                     /* The sum of the sizes of all maximal traces */

    /**
     * Construct a new instance of the exploration algorithm for an event structure with a fixed number of threads
     *
     * @param es
     * @param contPred
     */
    public Algorithm1(EventStructure es, int numThreads) {
        this.es = es;
        this.E = Sets.newTreeSet();
        E.add(es.getRoot());                   /* Add ⊥ to the set of discovered events */
        this.numThreads = numThreads;
        this.traceCount = 0;
        this.traceSizeSum = 0;
    }

    /**
     * Construct a new instance of the exploration algorithm for an event structure with a fixed number of threads
     *
     * @param es
     * @param contPred
     */
    public Algorithm1(EventStructure es) {
        this.es = es;
        this.E = Sets.newTreeSet();
        E.add(es.getRoot());                   /* Add ⊥ to the set of discovered events */
        this.traceCount = 0;
        this.traceSizeSum = 0;
    }

    public int getLfsNumber() {
        return lfsNumber;
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
        if (D.stream().allMatch(new Predicate<EventStructure.Event>() {
            public boolean test(final EventStructure.Event eventD) {
                return C.stream().anyMatch(new Predicate<EventStructure.Event>() {
                    public boolean test(EventStructure.Event eventC) {
                        return eventC.isInConflict(eventD);
                    }
                });
            }
        })) {
            return C;
        } else {
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

        System.out.println("C=" + C + " | D=" + D + " | A=" + A);

        E.addAll(es.getExtensions(C));

        if (es.isMaximalConf(C)) {
            if (numThreads != 0) {
                for (EventStructure.Event maximalEvent : EventStructure.getMaximalEvents(C)) {
                    Set<EventStructure.Event> cone = maximalEvent.getCone();
                    int coneWidth = EventStructure.getWidth(cone, numThreads);
                    if (lfsNumber < coneWidth)
                        lfsNumber = coneWidth;
                }
            }
            traceSizeSum += C.size();
            traceCount++;
            return;
        }

        Set<EventStructure.Event> setE;
        if (!A.isEmpty()) {
            setE = ImmutableSet.copyOf(Iterables.limit(Sets.intersection(A, es.getEnabled(C)), 1));
        } else {
            setE = ImmutableSet.copyOf(Iterables.limit(es.getEnabled(C), 1));
        }

        Set<EventStructure.Event> newC = Sets.union(C, setE);

        explore(newC, D, Sets.difference(A, setE));

        Set<EventStructure.Event> altConf = searchAlt(C, Sets.union(D, setE));
        if (!altConf.isEmpty()) {
            explore(C, Sets.union(D, setE), Sets.difference(altConf, C));
        }
    }
}
