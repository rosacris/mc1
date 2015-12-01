package org.cifasis.mc1;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Cristian on 22/07/15.
 */
public class Algorithm1 {

    private final EventStructure es;                              /* the event structure to explore by the algorithm. */
    private final Set<Event> E;                    /* the set of events discovered by the algorithm. */
    private int lfsNumber = 0;
    private int numThreads = 0;
    private int traceCount;                                       /* The number of explored traces */
    private int traceSizeSum;                                     /* The sum of the sizes of all maximal traces */

    /**
     * Construct a new instance of the exploration algorithm for an event structure with a fixed number of threads
     *
     * @param es
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
     */
    public Algorithm1(EventStructure es) {
        this.es = es;
        this.E = Sets.newTreeSet();
        E.add(es.getRoot());                   /* Add ⊥ to the set of discovered events */
        this.traceCount = 0;
        this.traceSizeSum = 0;
    }

    public Set<Event> getE() {
        return E;
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
     * @param A the set of old alternatives from which event was selected.
     * @param event the event entering D from C
     * @return a valid alternative configuration, or empty set if not found.
     */
    private Set<Event> searchAlt(final Set<Event> C, final Set<Event> D, final Set<Event> A, final Event event) {


        // Set of events to justify (e and the direct conflicts of e in D).
        Set<Event> toJustifyFromEvent = Sets.union(Sets.intersection(event.getDirectConflicts(), D), ImmutableSet.of(event));

        Set<Event> toJustifyFromA = D.stream().filter(d -> !Sets.intersection(A, d.getDirectConflicts()).isEmpty()).collect(Collectors.toSet());

        Set<Event> eventsToJustify = Sets.union(toJustifyFromEvent, toJustifyFromA);

        // List of event sets from which the alternative must be computed (if exists).
        List<Set<Event>> conflictVectors = eventsToJustify.stream().distinct().map(
                e -> e.getDirectConflicts().stream().filter(eConfl -> !D.contains(eConfl)).collect(Collectors.toSet())
        ).collect(Collectors.toList());

        // Alternatives computation
        return Sets.cartesianProduct(conflictVectors).stream().map(
                eventList -> eventList.stream().map(Event::getCone).flatMap(Collection::stream).collect(Collectors.toSet())
        ).filter(alternative -> Sets.intersection(D, alternative).isEmpty()
                && EventStructure.isConf(Sets.union(C, alternative))
                && D.stream().allMatch(
                    d -> !Sets.intersection(Sets.union(C, alternative), d.getDirectConflicts()).isEmpty()
                )).findFirst().orElse(Sets.newHashSet());
    }

    /**
     * The exploration algorithm.
     *
     * @param C the initial configuration.
     * @param D a set of disabled events.
     * @param A a set of events to steer the exploration.
     */
    public void explore(final Set<Event> C, final Set<Event> D, Set<Event> A) {

//        System.out.println("C=" + C + " | D=" + D + " | A=" + A);

        E.addAll(es.getExtensions(C));

        if (es.isMaximalConf(C)) {
            if (numThreads != 0) {
                for (Event maximalEvent : EventStructure.getMaximalEvents(C)) {
                    Set<Event> cone = maximalEvent.getCone();
                    int coneWidth = EventStructure.getWidth(cone, numThreads);
                    if (lfsNumber < coneWidth)
                        lfsNumber = coneWidth;
                }
            }
            traceSizeSum += C.size();
            traceCount++;

            return;
        }

        Set<Event> setE;
        if (!A.isEmpty()) {
            setE = ImmutableSet.copyOf(Iterables.limit(Sets.intersection(A, es.getEnabled(C)), 1));
        } else {
            setE = ImmutableSet.copyOf(Iterables.limit(es.getEnabled(C), 1));
        }

        Set<Event> newC = Sets.union(C, setE);

        explore(newC, D, Sets.difference(A, setE));

        Set<Event> altConf = searchAlt(C, Sets.union(D, setE), A, setE.iterator().next());
        if (!altConf.isEmpty()) {
            explore(C, Sets.union(D, setE), Sets.difference(altConf, C));
        }
    }
}
