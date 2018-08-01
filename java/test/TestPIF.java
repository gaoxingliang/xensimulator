import com.xensource.xenapi.Host;
import com.xensource.xenapi.PIF;
import junit.framework.Assert;

import java.util.Map;

public class TestPIF extends TestCase{

    @Override
    public void test() throws Exception {
        Map<PIF, PIF.Record> pifs = PIF.getAllRecords(c);
        Host h = Host.getByUuid(c, "4ac188d2-7dfa-44ae-8e6f-3b88d75220ce");
        Double d = h.queryDataSource(c, "pif_eth0_tx");
        System.out.println("the tx for eth0 is " + d);
        Assert.assertTrue("should get value for pif", d != null);
    }
}
