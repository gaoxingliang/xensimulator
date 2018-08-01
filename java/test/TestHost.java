import com.xensource.xenapi.Host;
import com.xensource.xenapi.HostMetrics;
import junit.framework.Assert;
import org.junit.Test;

public class TestHost extends TestCase {

    @Test
    public void test() throws Exception {
        Host h = Host.getByUuid(c, "4ac188d2-7dfa-44ae-8e6f-3b88d75220ce");
        HostMetrics hm = h.getMetrics(c);
        HostMetrics.Record r = hm.getRecord(c);
        Assert.assertTrue(r.live);
        Assert.assertEquals(1024 * 1024 * 1024L, r.memoryTotal.longValue());
    }
}
