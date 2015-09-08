import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cifasis.mc1.EventStructure;
import org.cifasis.mc1.PoetLexer;
import org.cifasis.mc1.PoetParser;
import org.cifasis.mc1.poet.org.cifasis.mc1.PoetInput;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by cristian on 08/09/15.
 */
public class PoetInputTest {

    @Test
    public void test() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(getClass().getResourceAsStream("poetInput.txt"));
        PoetLexer lexer = new PoetLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PoetParser parser = new PoetParser(tokens);
        ParseTree tree = parser.events();

        EventStructure es = new EventStructure();
        PoetInput poetInput = new PoetInput(es);
        poetInput.visit(tree);

        System.out.println(es.toString());

    }


}
