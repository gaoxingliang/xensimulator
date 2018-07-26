import com.xensource.xenapi.Connection;
import com.xensource.xenapi.Session;

import java.net.URL;

public class TestXenLogin {
    public static void main(String[] args) throws Exception {
        Connection c = new Connection(new URL("http://127.0.0.1:8080/xmlrpc"));
        Session.loginWithPassword(c, "a", "b");
    }
}
