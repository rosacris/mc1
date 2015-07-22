import com.google.common.collect.Sets;
import org.cifasis.mc1.EventStructure;
import org.junit.Test;

/**
 * Created by cristian on 17/07/15.
 */
public class EventStructureTest {

    @Test
    public void test1() {
        EventStructure es = new EventStructure();
        EventStructure.Event root = es.getRoot();

        EventStructure.Event e1 = es.newEvent("e1").dependsOn(root);
        EventStructure.Event e2 = es.newEvent("e2").dependsOn(root);
        EventStructure.Event e3 = es.newEvent("e3").dependsOn(e1).conflicts(e2);
        EventStructure.Event e4 = es.newEvent("e4").dependsOn(e3);

        assert(e4.isDependent(e1));
        assert(e4.isInConflict(e2));
        assert(!e2.isInConflict(e1));
        System.out.println(es.toString());
        System.out.println(e4.getCone());
        assert(!EventStructure.isConf(Sets.newHashSet(root, e1, e2, e4)));
        assert(es.isMaximalConf(Sets.newHashSet(root, e1, e3, e4)));
        assert(!es.isMaximalConf(Sets.newHashSet(root, e1, e3)));
        assert(EventStructure.isConf(Sets.newHashSet(root, e1, e2)));
        assert(es.isMaximalConf(Sets.newHashSet(root, e1, e2)));
        System.out.println(es.getExtensions(Sets.newHashSet(root, e1, e3)));

    }

}
