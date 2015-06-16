import org.cifasis.mc1.Int;
import org.cifasis.mc1.MCProcess;
import org.cifasis.mc1.MCState;
import org.junit.Test;

/**
 * Created by Cristian on 16/06/15.
 */
public class MCProcessTest {

    /**
     * Test the suspend/resume interface and the local states consistency
     * @throws InterruptedException
     */
    @Test
    public void test1() throws InterruptedException {

        MCProcess p1 = new MCProcess("p1") {
            public void run() {
                Int myVar = localMCState.newVar("myVar", 5);
                suspend();
                myVar.set(10);
                suspend();
            }
        };

        p1.start();
        Thread.sleep(2);
        MCState s1 = p1.getMCState();
        p1.resume();
        Thread.sleep(2);
        MCState s2 = p1.getMCState();
        p1.resume();
        assert(!s1.equals(s2));
        assert(s1.getVar("myVar").get() == 5);
        assert(s2.getVar("myVar").get() == 10);
        System.out.println(s1);
        System.out.println(s2);
    }
}
