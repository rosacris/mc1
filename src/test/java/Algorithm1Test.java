import com.google.common.collect.Sets;
import org.cifasis.mc1.Algorithm1;
import org.cifasis.mc1.EventStructure;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by cristian on 22/07/15.
 */
public class Algorithm1Test {

    @Test
    public void test1() {
        EventStructure es = new EventStructure();

        EventStructure.Event e1 = es.newEvent("e1").dependsOn(es.getRoot());
        EventStructure.Event e2 = es.newEvent("e2").dependsOn(e1);
        EventStructure.Event e3 = es.newEvent("e3").dependsOn(es.getRoot()).conflicts(e1);
        EventStructure.Event e4 = es.newEvent("e4").dependsOn(e3);
        EventStructure.Event e5 = es.newEvent("e5").dependsOn(es.getRoot());
        EventStructure.Event e6 = es.newEvent("e6").dependsOn(e5);
        EventStructure.Event e7 = es.newEvent("e7").dependsOn(es.getRoot()).conflicts(e5);
        EventStructure.Event e8 = es.newEvent("e8").dependsOn(e7);

        Algorithm1 alg1 = new Algorithm1(es);
        Set<EventStructure.Event> C = Sets.newTreeSet();
        C.add(es.getRoot());
        alg1.explore(C, new TreeSet<EventStructure.Event>(), new TreeSet<EventStructure.Event>());

    }


}
