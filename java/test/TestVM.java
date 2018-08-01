import com.xensource.xenapi.Session;
import com.xensource.xenapi.VM;
import com.xensource.xenapi.VMMetrics;
import junit.framework.Assert;

public class TestVM extends TestCase {
    @Override
    public void test() throws Exception {
        System.out.println("=======");
        VM vm = VM.getByUuid(c, "ec944b8d-861e-ac96-ec47-5f56df8ef772");
        System.out.println(vm);
        VMMetrics.Record r = vm.getMetrics(c).getRecord(c);
        System.out.println(r);
        Session.logout(c);
        Assert.assertTrue("The memory should be >=0 & <=1204 but is:" + r.memoryActual, r.memoryActual >= 0 && r.memoryActual <= 1024);
    }
}
