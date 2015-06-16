import org.cifasis.mc1.Int;
import org.cifasis.mc1.MCState;
import org.junit.Test;

/**
 * Created by cristian on 15/06/15.
 */
public class MCStateTest {
    @Test
    public void experiment1() {
        MCState s1 = new MCState();
        Int a = s1.newVar("a", 5);
        Int b = s1.newVar("b", 6);

        MCState s2 = new MCState();
        Int c = s2.newVar("a", 5);
        Int d = s2.newVar("b", 6);

        assert(s1.equals(s2));
    }
}
