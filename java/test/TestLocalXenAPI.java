import com.xensource.xenapi.*;

import java.net.URL;

public class TestLocalXenAPI {
    public static void main(String[] args) throws Exception {
        boolean local = false;
        Connection c = null;
        Session s = null;
        c = new Connection(new URL("http://192.168.170.151"));
        s = Session.loginWithPassword(c, "root", "123456", APIVersion.latest().toString());

        Host.getAllRecords(c).entrySet().stream().forEach(e -> {
            Host h = e.getKey();
            System.out.println(e.getValue().toMap());
        });

        System.out.println(Pool.getAllRecords(c));
        Session.logout(c);
    }
}
