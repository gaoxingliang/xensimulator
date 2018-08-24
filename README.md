A Xen Simulator which use the XMLRPC server framework to simulate response for xen server devices.<br>
This is useful for test case purpose.<br>
<b>Notes:</b>
- This project works with xen-server SDK 5.5
- There are two jars under [libs](libs/) directory. one is xen-server 5.5 SDK jar. another is a build of changed apache-xmlrpcserver. See the "How to configure part" for more details.


# Features
- Support http and https
- Set the values from remote side
- Support get requests and customize the response
- Full support objects: Host/CPU/PIF/VM/VCPU/VBD/VIF/SR/Pool/VMRRD/HOSTRRD

# How to build
Build it with:
```
gradle jar
```
or a full jar contains dependencies:
```
gradle fatjar
```

# How to use
```java
package com.logicmonitor.xensimulator.server.api;

import com.logicmonitor.xensimulator.server.XenSimulator;
import com.logicmonitor.xensimulator.utils.SSLUtils;
import com.logicmonitor.xensimulator.utils.XenSimulatorSettings;
import com.xensource.xenapi.APIVersion;
import com.xensource.xenapi.Connection;
import com.xensource.xenapi.Host;
import com.xensource.xenapi.Session;

import java.net.URL;

public class Example {
    public static void main(String[] args) throws Exception {
        XenSimulatorSettings.ignoreSSL = true;
        int httpsPort = 8080, httpPort = 9090;
        XenSimulator simulator = new XenSimulator(httpsPort, httpPort, "test", "testpass");
        simulator.start();
        SSLUtils.ignoreSSL(8080);
        Connection c = new Connection(new URL(String.format("https://127.0.0.1:%d/", httpsPort)));
        Session s = Session.loginWithPassword(c, "test", "testpass", APIVersion.latest().toString());
        System.out.println("All hosts:" + Host.getAllRecords(c));
        simulator.stop();
    }
}

```

# How to configure
### set the values per xml
I provided a func for remote side to change the value for test purpose.<br>
Now all [xmltemplates](resources/xmltemplates) (excepct *_rrd.xml) could be changed by remote RPC call:
```
// change the value of xml
XenRPCClient client = new XenRPCClient("https://127.0.0.1:" + simulator.httpsPort);

String vmInstance = "OpaqueRef:8a38e59d-c143-16e1-c475-30324368d589";
Object updateIsATemplateResult = client.request(XenSimulatorSettings.class.getName(), "updateXmlFileContent", new
        Object[]{"/xmltemplates/VM_all_records.xml", "$Value$" + vmInstance + "$is_a_template", true});
System.out.println("Update the xml response " + updateIsATemplateResult);
```
This method has a xml path argument which has a format:
```
 1. the students[0]'s name
    The path is: $Value$students$0
 2. the avgscore:
    The path is: $Value$avgscore
    That's to say, for a array, the format should like:
          $arrayName$arrayIndex$objectAttr
```


All methods has an annotation <b>@SimAPI</b> indicates you can call it remotely to change the simulator internal status.<br>
All methods has an annotation <b>@API</b> indicates this is a method which may be called from remote XEN SDK client.

### set the get request response
By default, the XML RPC only supports POST request, so I changed the implementation of apache XMLRPC server framework [github](https://github.com/gaoxingliang/apache-xmlrpc-3.1.3-src) to support this.<br>
You can change the response by register a:
```
XenSimulatorSettings.getRequestHandler
```
OR register a uri pattern from remote side by call with similar code with previous XenRPCClient:
```
XenSimulatorSettings.regUriPattern2Response(String uriRegexPattern, String response)
```

# TODO
Support latest sdk.

# others
## new a keystore file
```
keytool -genkey -keystore xensim.keystore -keyalg RSA
```
