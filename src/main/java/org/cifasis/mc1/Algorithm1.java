package org.cifasis.mc1;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by Cristian on 22/07/15.
 */
public class Algorithm1 {

    private final EventStructure es;

    private final Set<EventStructure.Event> E;

    public Algorithm1(EventStructure es) {
        this.es = es;
        this.E = Sets.newHashSet(es.getRoot());
    }

    private static Set<EventStructure.Event> searchConf(EventStructure es, Set<EventStructure.Event> C, Set<EventStructure.Event> D) {
        for(EventStructure.Event e : es.getExtensions(C)){
            if(!D.contains(e) && EventStructure.isConf(Sets.union(C, Sets.newHashSet(e)))) {
                return Sets.newTreeSet(Sets.union(C, Sets.newHashSet(e)));
            }
        }
        return Sets.newTreeSet();
    }

    public void explore(Set<EventStructure.Event> C, Set<EventStructure.Event> D, Set<EventStructure.Event> A) {

        System.out.println(C + " | " + D + " | " + A + " ~~ " + Sets.difference(C, es.getEventSet()));

        if (es.isMaximalConf(C))
            return;

        E.addAll(es.getExtensions(C));

        Set<EventStructure.Event> e;
        if (!A.isEmpty())
            e = Sets.newHashSet(Sets.intersection(es.getExtensions(C), A).iterator().next());
        else
            e = Sets.newHashSet(es.getExtensions(C).iterator().next());

        explore(Sets.union(C, e), D, Sets.difference(A, e));
        Set<EventStructure.Event> newConf = searchConf(es, C, Sets.union(D,e));
        if(!newConf.isEmpty()) {
            explore(C, Sets.union(D, e), Sets.difference(newConf, C));
        }
//        if (Sets.intersection(C, D).isEmpty() && !Sets.difference(Sets.difference(es.getExtensions(C), D), e).isEmpty())
//            explore(C, Sets.union(D, e), Sets.difference(Sets.difference(es.getExtensions(C), D), e));
    }

}
