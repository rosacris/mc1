import com.google.common.collect.Sets;
import org.cifasis.mc1.Event;
import org.cifasis.mc1.EventStructure;
import org.junit.Test;

/**
 * Created by Cristian on 17/07/15.
 */
public class EventStructureTest {

    @Test
    public void test1() {
        EventStructure es = new EventStructure();
        Event root = es.getRoot();
        Event e1 = es.newEvent("e1").dependsOn(root);
        Event e2 = es.newEvent("e2").dependsOn(root);
        Event e3 = es.newEvent("e3").dependsOn(e1).conflictsWith(e2);
        Event e4 = es.newEvent("e4").dependsOn(e3);

        System.out.println(es.toString());

        /* This assertions test the public API of the event structure class */
        assert (e4.isDependent(e1));
        assert (e4.isInConflict(e2));
        assert (!e2.isInConflict(e1));
        assert (e4.getCone().equals(Sets.newHashSet(root, e1, e3)));
        assert (!EventStructure.isConf(Sets.newHashSet(root, e1, e2, e4)));
        assert (es.isMaximalConf(Sets.newHashSet(root, e1, e3, e4)));
        assert (!es.isMaximalConf(Sets.newHashSet(root, e1, e3)));
        assert (EventStructure.isConf(Sets.newHashSet(root, e1, e2)));
        assert (es.isMaximalConf(Sets.newHashSet(root, e1, e2)));
        assert (es.getExtensions(Sets.newHashSet(root, e1, e3)).equals(Sets.newHashSet(e2, e4)));
        assert (es.getEnabled(Sets.newHashSet(root, e1, e3)).equals(Sets.newHashSet(e4)));
        assert (EventStructure.getMaximalEvents(Sets.newHashSet(root, e1, e2)).equals(Sets.newHashSet(e1, e2)));
    }
}
