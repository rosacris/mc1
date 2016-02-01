package org.cifasis.mc1;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import jdk.nashorn.internal.runtime.options.Option;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by cristian on 29/07/15.
 */
public class Algorithm2 {

    private final EventStructure es;                              /* the event structure to explore by the algorithm. */
    private final Set<Event> E;                    /* the set of events discovered by the algorithm. */
    private Set<Event> V;                          /* the set of visited events */
    private Set<Event> Vtest;                      /* this variable is just used for testing porpouses */
    private final Predicate<Set<Event>> contPred;  /* a predicate to decide the backtracking. */
    private final int lfsBound;
    private int traceCount;                                       /* The number of explored traces */
    private int traceSizeSum;                                     /* The sum of the sizes of all maximal traces */
    private List visitedRatio;                           /* keep track of the ratio between the whole set of visited events and the ones we keep in V when a maximal conf is reached */

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
        this.Vtest = Sets.newHashSet(es.getRoot());
        this.lfsBound = computeBound(m, n);
        this.traceCount = 0;
        this.visitedRatio = new java.util.ArrayList();
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
        this.Vtest = Sets.newHashSet(es.getRoot());
        this.lfsBound = lfsBound;
        this.traceCount = 0;
        this.visitedRatio = new java.util.ArrayList();
    }

    /**
     * This predicate always continue.
     */
    public static final Predicate<Set<Event>> ALWAYS_CONTINUE = new Predicate<Set<Event>>() {
        public boolean test(Set<Event> conf) {
            return true;
        }
    };

    /**
     * This predicate enforces a LFS-bound of max(C) <= lfsBound.
     */
    public final Predicate<Set<Event>> LFS_BOUND = new Predicate<Set<Event>>() {
        public boolean test(Set<Event> conf) {
//            System.out.println("Continue?: " + conf + " , maximal: " + EventStructure.getMaximalEvents(conf));
            return EventStructure.getMaximalEvents(conf).size() <= lfsBound;
        }
    };

    public Set<Event> getE() {
        return E;
    }

    public Set<Event> getVtest() {
        return Vtest;
    }

    public int getTraceCount() {
        return this.traceCount;
    }

    public int getTraceSizeAvg() {
        return traceSizeSum / traceCount;
    }

    public List getVisitedRatio() { return visitedRatio; }

    /**
     * Search for alternative configurations that contain a given configuration and remain to be explored.
     *
     * @param C the configuration that must be contained by the alternative.
     * @param D the set of disabled events that must not appear in the alternative.
     * @param A the set of old alternatives from which event was selected.
     * @param event the event entering D from C
     * @return a valid alternative configuration, or empty set if not found.
     */
    private Set<Set<Event>> searchAlt(final Set<Event> C, final Set<Event> D, final Set<Event> A, final Event event) {


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
        )).collect(Collectors.toSet());
    }

    /**
     * The exploration algorithm.
     *
     * @param C the initial configuration.
     * @param D a set of disabled events.
     * @param A a set of events to steer the exploration.
     */

    public void explore(final Set<Event> C, final Set<Event> D, Set<Event> A) {

        //System.out.println("C=" + C + " | D=" + D + " | A=" + A + " | en=" + es.getEnabled(C) + " | E=" + E);

        E.addAll(es.getExtensions(C));

        Set<Event> eSet;
        if (!A.isEmpty() && !Sets.difference(A, V).isEmpty()) {
            eSet = ImmutableSet.copyOf(Iterables.limit(Sets.intersection(A, es.getEnabled(C)), 1));
        } else {
            Optional<Event> e = es.getEnabled(C).stream().filter(event -> !D.contains(event) && (!V.contains(event) || contPred.test(Sets.union(C, Sets.newHashSet(event))))).findFirst();
            if (e.isPresent()) {
                eSet = ImmutableSet.of(e.get());
            } else {
                traceSizeSum += C.size();
                traceCount++;
                visitedRatio.add((float) V.size() / Vtest.size());
                //System.out.println();
                return;
            }
        }

        Set<Event> newC = Sets.union(C, eSet);

        V.add(eSet.iterator().next());
        Vtest.add(eSet.iterator().next());

        explore(newC, D, Sets.difference(A, eSet));

        // Look for alternatives to the actual configuration
        Set<Set<Event>> alternatives = searchAlt(C, Sets.union(D, eSet), A, eSet.iterator().next());

        // Get an alternative that stays in the bound or that it contains unvisited events.
        Set<Event> altConf = alternatives.stream().filter(contPred::test).findFirst().orElse(
            alternatives.stream().filter(alt -> !Sets.difference(alt, V).isEmpty()).findFirst().orElse(null));

        if (altConf != null) {
            Set<Event> toRemove = Sets.union(D, eSet).stream().map(event -> es.getSuccessors(V, event)).flatMap(Collection::stream).collect(Collectors.toSet());;
//            Set<Event> toRemove = es.getSuccessors(V2, eSet.iterator().next());
            V.removeAll(toRemove);
            explore(C, Sets.union(D, eSet), Sets.difference(altConf, C));
        }

    }

    public static int computeBound(int m, int n) {
        return ((int) ((n - 1) * (Math.log(m) / Math.log(n))) + 1);
    }
}
