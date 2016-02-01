import com.google.common.collect.Sets;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cifasis.mc1.*;
import org.cifasis.mc1.PoetLexer;
import org.cifasis.mc1.PoetParser;
import org.cifasis.mc1.poet.org.cifasis.mc1.PoetInput;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by cristian on 22/07/15.
 */
@FixMethodOrder(MethodSorters.JVM)
public class Algorithm2Test {

    private Algorithm2 explore(EventStructure es, int m, int n) {
        //System.out.println(es.toString());
        System.out.println("m=" + m + " n=" + n + " lfs-bound=" + Algorithm2.computeBound(m, n));
        Algorithm2 algorithm = new Algorithm2(es, m, n);
        Set<Event> C = Sets.newTreeSet();
        C.add(es.getRoot());
        algorithm.explore(C, new TreeSet<Event>(), new TreeSet<Event>());
        System.out.println("ES events count: " + es.getEventSet().size());
        System.out.println("Visited events count: " + algorithm.getVtest().size());
        System.out.println("Trace count: " + algorithm.getTraceCount());
        System.out.println("Trace size avg: " + algorithm.getTraceSizeAvg());
        System.out.println("Visited: " + algorithm.getVisitedRatio());
        assert (es.getEventSet().size() == algorithm.getVtest().size());
        return algorithm;
    }

    private Algorithm2 explore(EventStructure es, int lfsBound) {
        //System.out.println(es.toString());
        System.out.println("Manual lfs-bound=" + lfsBound);
        Algorithm2 algorithm = new Algorithm2(es, lfsBound);
        Set<Event> C = Sets.newTreeSet();
        C.add(es.getRoot());
        algorithm.explore(C, new TreeSet<Event>(), new TreeSet<Event>());
        System.out.println("ES events count: " + es.getEventSet().size());
        System.out.println("Visited events count: " + algorithm.getVtest().size());
        System.out.println("Trace count: " + algorithm.getTraceCount());
        System.out.println("Trace size avg: " + algorithm.getTraceSizeAvg());
        System.out.println("Visited: " + algorithm.getVisitedRatio());
        assert (es.getEventSet().size() == algorithm.getVtest().size());
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
        Event e1 = es.newEvent("1").dependsOn(es.getRoot());
        Event e2 = es.newEvent("2").dependsOn(e1);
        Event e3 = es.newEvent("3").dependsOn(es.getRoot()).conflictsWith(e1);
        Event e4 = es.newEvent("4").dependsOn(e3);
        Event e5 = es.newEvent("5").dependsOn(es.getRoot());
        Event e6 = es.newEvent("6").dependsOn(e5);
        Event e7 = es.newEvent("7").dependsOn(es.getRoot()).conflictsWith(e5);
        Event e8 = es.newEvent("8").dependsOn(e7);

        explore(es, 2, 1);
    }

    @Test
    public void test2() {
        System.out.println();
        System.out.println("Test 2");
        EventStructure es = new EventStructure();
        Event e1 = es.newEvent("1").dependsOn(es.getRoot());
        Event e2 = es.newEvent("2").dependsOn(es.getRoot());
        Event e3 = es.newEvent("3").dependsOn(es.getRoot());
        Event e4 = es.newEvent("4").dependsOn(e2).dependsOn(e3);

        explore(es, 3, 2);
    }

    @Test
    public void test3() {
        System.out.println();
        System.out.println("Test 3");
        EventStructure es = new EventStructure();
        Event e1 = es.newEvent("1").dependsOn(es.getRoot());
        Event e2 = es.newEvent("2").dependsOn(es.getRoot()).conflictsWith(e1);
        Event e3 = es.newEvent("3").dependsOn(es.getRoot());
        Event e4 = es.newEvent("4").dependsOn(es.getRoot()).conflictsWith(e3);
        Event e5 = es.newEvent("5").dependsOn(e2).dependsOn(e3);
        explore(es, 2);
    }

    @Test
    public void doubleDiamond() {
        System.out.println();
        System.out.println("Double diamond");
        EventStructure es = new EventStructure();
        Event e1 = es.newEvent("1").dependsOn(es.getRoot());
        Event e2 = es.newEvent("2").dependsOn(es.getRoot());
        Event e3 = es.newEvent("3").dependsOn(e1);
        Event e4 = es.newEvent("4").dependsOn(e1);
        Event e5 = es.newEvent("5").dependsOn(e2);
        Event e6 = es.newEvent("6").dependsOn(e2);
        Event e7 = es.newEvent("7").dependsOn(e3).dependsOn(e4);
        Event e8 = es.newEvent("8").dependsOn(e5).dependsOn(e6);
        explore(es, 2);
    }

    @Test
    public void stf() throws IOException {
        System.out.println();
        System.out.println("STF");
        EventStructure es = fromPoet("poet_stf.txt");
        explore(es, 3, 2);
    }

    @Test
    public void spin08() throws IOException {
        System.out.println();
        System.out.println("SPIN08");
        EventStructure es = fromPoet("poet_spin08.txt");
        explore(es, 2);
    }

    @Test
    public void ssb() throws IOException {
        System.out.println();
        System.out.println("SSB");
        EventStructure es = fromPoet("poet_ssb.txt");
        explore(es, 4);
    }

//    @Test
//    public void ssbextra() throws IOException {
//        System.out.println();
//        System.out.println("SSB-EXTRA-THREAD");
//        EventStructure es = fromPoet("poet_ssb_extra_thread.txt");
//        //System.out.println(es.toDot(es.getEventByName("41").getCone()));
//        explore(es, 4);
//    }

    @Test
    public void ssb1() throws IOException {
        System.out.println();
        System.out.println("SSB1");
        EventStructure es = fromPoet("poet_ssb1.txt");
        explore(es, 4);
    }

    @Test
    public void ssb3() throws IOException {
        System.out.println();
        System.out.println("SSB3");
        EventStructure es = fromPoet("poet_ssb3.txt");
        Algorithm2 alg = explore(es, 4);
    }

    @Test
    public void ccnf9() throws IOException {
        System.out.println();
        System.out.println("CCNF9");
        EventStructure es = fromPoet("poet_ccnf9.txt");
        //System.out.println(es.toDot());
        explore(es, 2);
    }

    @Test
    public void ccnf17() throws IOException {
        System.out.println();
        System.out.println("CCNF17");
        EventStructure es = fromPoet("poet_ccnf17.txt");
        explore(es, 2);
    }

    @Test
    public void ccnf19() throws IOException {
        System.out.println();
        System.out.println("CCNF19");
        EventStructure es = fromPoet("poet_ccnf19.txt");
        explore(es, 2);
    }

    @Test
    public void szymanski() throws IOException {
        System.out.println();
        System.out.println("SZYMANSKI");
        EventStructure es = fromPoet("poet_szymanski.txt");
        explore(es, 2);
    }

    @Test
    public void pgsql() throws IOException {
        System.out.println();
        System.out.println("PGSQL");
        EventStructure es = fromPoet("poet_pgsql.txt");
        explore(es, 2);
    }

    @Test
    public void peterson() throws IOException {
        System.out.println();
        System.out.println("PETERSON");
        EventStructure es = fromPoet("poet_peterson.txt");
        explore(es, 2);
    }

    @Test
    public void prodcons2() throws IOException {
        System.out.println();
        System.out.println("PRODCONS2");
        EventStructure es = fromPoet("poet_prodcons2.txt");
        explore(es, 3);
    }
}