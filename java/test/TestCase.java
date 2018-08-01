import com.logicmonitor.xensimulator.server.XenSimulator;
import com.logicmonitor.xensimulator.utils.SSLUtils;
import com.logicmonitor.xensimulator.utils.SimulatorSettings;
import com.xensource.xenapi.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

public abstract class TestCase {
    protected XenSimulator simulator;
    protected Session s;
    protected Connection c;

    @Before
    public void start() throws Exception {
        SimulatorSettings.ignoreSSL = true;
        simulator = new XenSimulator(8080, "test", "testpass");
        simulator.start();
        SSLUtils.ignoreSSL(8080);
        c = new Connection(new URL("https://127.0.0.1:8080/"));
        s = Session.loginWithPassword(c, "test", "testpass", APIVersion.latest().toString());
    }

    @After
    public void shutdown() {
        if (simulator != null) {
            simulator.stop();
        }
    }

    @Test
    public abstract void test() throws Exception;
}
