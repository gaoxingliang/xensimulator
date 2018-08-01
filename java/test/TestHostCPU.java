import com.xensource.xenapi.Host;
import com.xensource.xenapi.HostCpu;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TestHostCPU extends TestCase {

    @Test
    public void test() throws Exception {
        Map<HostCpu, HostCpu.Record> cpuRecords = HostCpu.getAllRecords(c);
        Map<String, HostCpu.Record> records = new HashMap<String, HostCpu.Record>();
        for (HostCpu.Record r : cpuRecords.values()) {
            records.put(r.uuid, r);
        }

        for (HostCpu.Record cpuRecord : cpuRecords.values()) {
            System.out.println(cpuRecord.number + " uuid:" + cpuRecord.uuid);
            Host h = cpuRecord.host;
            double value = h.queryDataSource(c, "cpu" + cpuRecord.number);
            System.out.println(value);
        }
    }
}
