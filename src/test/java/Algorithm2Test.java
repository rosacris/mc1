import com.google.common.collect.Sets;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cifasis.mc1.Algorithm2;
import org.cifasis.mc1.EventStructure;
import org.cifasis.mc1.PoetLexer;
import org.cifasis.mc1.PoetParser;
import org.cifasis.mc1.poet.org.cifasis.mc1.PoetInput;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by cristian on 29/07/15.
 */
public class Algorithm2Test {

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

        Algorithm2 algorithm = new Algorithm2(es, 1);
        Set<EventStructure.Event> C = Sets.newTreeSet();
        C.add(es.getRoot());
        algorithm.explore(C, new TreeSet<EventStructure.Event>(), new TreeSet<EventStructure.Event>());

    }

    @Test
    public void test2() {
        EventStructure es = new EventStructure();

        EventStructure.Event e1 = es.newEvent("1").dependsOn(es.getRoot());
        EventStructure.Event e2 = es.newEvent("2").dependsOn(es.getRoot());
        EventStructure.Event e3 = es.newEvent("3").dependsOn(es.getRoot());
        EventStructure.Event e4 = es.newEvent("4").dependsOn(e2).dependsOn(e3);

        Algorithm2 algorithm = new Algorithm2(es, 2);
        Set<EventStructure.Event> C = Sets.newTreeSet();
        C.add(es.getRoot());
        algorithm.explore(C, new TreeSet<EventStructure.Event>(), new TreeSet<EventStructure.Event>());
    }

    @Test
    public void test3() throws IOException {
        EventStructure es = new EventStructure();

        ANTLRInputStream input = new ANTLRInputStream(getClass().getResourceAsStream("poet_ssb.txt"));
        PoetLexer lexer = new PoetLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PoetParser parser = new PoetParser(tokens);
        ParseTree tree = parser.events();
        PoetInput poetInput = new PoetInput(es);
        poetInput.visit(tree);

        System.out.println(es.toString());
//        System.out.println(es.getRoot().getDepsAsDot(Sets.<EventStructure.Event>newHashSet()));
//        System.out.println("conflicts");
//        System.out.println(es.getRoot().getConflictAsDot());

        Algorithm2 algorithm = new Algorithm2(es, 2);
        Set<EventStructure.Event> C = Sets.newTreeSet();
        C.add(es.getRoot());
        algorithm.explore(C, new TreeSet<EventStructure.Event>(), new TreeSet<EventStructure.Event>());
    }
}
