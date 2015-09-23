import com.google.common.collect.Sets;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cifasis.mc1.Algorithm1;
import org.cifasis.mc1.EventStructure;
import org.cifasis.mc1.PoetLexer;
import org.cifasis.mc1.PoetParser;
import org.cifasis.mc1.poet.org.cifasis.mc1.PoetInput;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by cristian on 22/07/15.
 */
@FixMethodOrder(MethodSorters.JVM)
public class Algorithm1Test {

    private Algorithm1 explore(EventStructure es, int numThreads) {
        System.out.println(es.toString());
        Algorithm1 algorithm = new Algorithm1(es, numThreads);
        Set<EventStructure.Event> C = Sets.newTreeSet();
        C.add(es.getRoot());
        algorithm.explore(C, new TreeSet<EventStructure.Event>(), new TreeSet<EventStructure.Event>());
        System.out.println("LFS-number=" + algorithm.getLfsNumber());
        return algorithm;
    }

    private Algorithm1 explore(EventStructure es) {
        System.out.println(es.toString());
        Algorithm1 algorithm = new Algorithm1(es);
        Set<EventStructure.Event> C = Sets.newTreeSet();
        C.add(es.getRoot());
        algorithm.explore(C, new TreeSet<EventStructure.Event>(), new TreeSet<EventStructure.Event>());
        System.out.println("LFS-number=" + algorithm.getLfsNumber());
        return algorithm;
    }

    private EventStructure fromPoet(String file) throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(getClass().getResourceAsStream(file));
        PoetLexer lexer = new PoetLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        org.cifasis.mc1.PoetParser parser = new PoetParser(tokens);
        ParseTree tree = parser.events();
        EventStructure es = new EventStructure();
        PoetInput poetInput = new PoetInput(es);
        poetInput.visit(tree);
        return es;
    }

    @Test
    public void test1() {
        System.out.println();
        System.out.println("Test 1");
        EventStructure es = new EventStructure();
        EventStructure.Event e1 = es.newEvent("1").dependsOn(es.getRoot());
        EventStructure.Event e2 = es.newEvent("2").dependsOn(e1);
        EventStructure.Event e3 = es.newEvent("3").dependsOn(es.getRoot()).conflicts(e1);
        EventStructure.Event e4 = es.newEvent("4").dependsOn(e3);
        EventStructure.Event e5 = es.newEvent("5").dependsOn(es.getRoot());
        EventStructure.Event e6 = es.newEvent("6").dependsOn(e5);
        EventStructure.Event e7 = es.newEvent("7").dependsOn(es.getRoot()).conflicts(e5);
        EventStructure.Event e8 = es.newEvent("8").dependsOn(e7);

        explore(es, 4);
    }

    @Test
    public void test2() {
        System.out.println();
        System.out.println("Test 2");
        EventStructure es = new EventStructure();
        EventStructure.Event e1 = es.newEvent("1").dependsOn(es.getRoot());
        EventStructure.Event e2 = es.newEvent("2").dependsOn(es.getRoot());
        EventStructure.Event e3 = es.newEvent("3").dependsOn(es.getRoot());
        EventStructure.Event e4 = es.newEvent("4").dependsOn(e2).dependsOn(e3);

        explore(es, 3);
    }

    @Test
    public void test3() {
        System.out.println();
        System.out.println("Test 3");
        EventStructure es = new EventStructure();
        EventStructure.Event e1 = es.newEvent("1").dependsOn(es.getRoot());
        EventStructure.Event e2 = es.newEvent("2").dependsOn(es.getRoot()).conflicts(e1);
        EventStructure.Event e3 = es.newEvent("3").dependsOn(es.getRoot());
        EventStructure.Event e4 = es.newEvent("4").dependsOn(es.getRoot()).conflicts(e3);
        EventStructure.Event e5 = es.newEvent("5").dependsOn(e2).dependsOn(e3);
        explore(es, 2);
    }

    @Test
    public void doubleDiamond() {
        System.out.println();
        System.out.println("Double diamond");
        EventStructure es = new EventStructure();
        EventStructure.Event e1 = es.newEvent("1").dependsOn(es.getRoot());
        EventStructure.Event e2 = es.newEvent("2").dependsOn(es.getRoot());
        EventStructure.Event e3 = es.newEvent("3").dependsOn(e1);
        EventStructure.Event e4 = es.newEvent("4").dependsOn(e1);
        EventStructure.Event e5 = es.newEvent("5").dependsOn(e2);
        EventStructure.Event e6 = es.newEvent("6").dependsOn(e2);
        EventStructure.Event e7 = es.newEvent("7").dependsOn(e3).dependsOn(e4);
        EventStructure.Event e8 = es.newEvent("8").dependsOn(e5).dependsOn(e6);
        explore(es, 4);
    }

    @Test
    public void stf() throws IOException {
        System.out.println();
        System.out.println("STF");
        EventStructure es = fromPoet("poet_stf.txt");
        explore(es, 3);
    }

    @Test
    public void ssb() throws IOException {
        System.out.println();
        System.out.println("SSB");
        EventStructure es = fromPoet("poet_ssb.txt");
        explore(es, 4);
    }

    @Test
    public void ccnf9() throws IOException {
        System.out.println();
        System.out.println("CCNF9");
        EventStructure es = fromPoet("poet_ccnf9.txt");
        explore(es);
    }

    @Test
    public void ccnf17() throws IOException {
        System.out.println();
        System.out.println("CCNF17");
        EventStructure es = fromPoet("poet_ccnf17.txt");
        explore(es);
    }

}
