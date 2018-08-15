package com.logicmonitor.xensimulator.utils;

import com.logicmonitor.xensimulator.Response;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class XenSimulatorSettings {

    public static final Logger LOG = LogManager.getLogger();

    // whether we will ignore ssl error for a client
    public static boolean ignoreSSL = true;

    /**
     * where the keystore file is.
     * Which will be relative with the class loader root path
     */
    public static String keystoreFile = "/xensim.keystore";

    /**
     * keystore password
     */
    public static char[] keystorePass = "123456".toCharArray();

    /**
     * the default get request response for http(s)://xxx:yyy/
     * by default, it contains a XenServer string
     */
    public static String getResponseContent = "";

    /**
     * a flag to indicate whether we need to add a delay when respond the request.
     * by default, it's 0 -> no delay
     */
    public static long responseDelayInMs = 0;


    // those files will be provided for remote side to update.
    private static final ConcurrentHashMap<String /*xml path relative to class loader*/, XMLResponse /*the parsed xml response*/> xml2Map = new ConcurrentHashMap<>();
    private static final String[] xmlRecordFiles = new String[]{
            "/xmltemplates/host_all_records.xml", "/xmltemplates/host_cpu_all_records.xml",
            "/xmltemplates/host_get_datasources.xml", "/xmltemplates/host_metrics_get_record.xml",
            "/xmltemplates/PIF_all_records.xml", "/xmltemplates/pool_all_records.xml",
            "/xmltemplates/SR_all_records.xml", "/xmltemplates/VBD_all_records.xml",
            "/xmltemplates/VIF_all_records.xml", "/xmltemplates/VM_all_records.xml",
            "/xmltemplates/VM_metrics_get_record.xml",
            "/xmltemplates/VM_query_datasource.xml"
    };

    public static final ConcurrentHashMap<String /*the get request uri regex pattern*/, String /*the response*/> uriRegex2Response = new ConcurrentHashMap<>();

    public static GetRequestHandler getRequestHandler = new DefaultGetRequestHandler();

    static {
        try {
            getResponseContent = IOUtils.toString(XenSimulatorSettings.class.getResourceAsStream("/getresponse.html"));
        }
        catch (IOException e) {
            LOG.error("Fail to load default response content for get request", e);
        }

        for (String xmlFile : xmlRecordFiles) {
            try {
                LOG.debug("Start loading {}", xmlFile);
                xml2Map.put(xmlFile, XMLResponse.parse(XenSimulatorSettings.class.getResourceAsStream(xmlFile)));
            }
            catch (Exception e) {
                LOG.error("Fail to load all xml files, please check it " + xmlFile, e);
                throw new IllegalStateException("Fail to load all xml files", e);
            }
        }
        LOG.info("All xmls loaded, count={}", xml2Map.size());
    }





    @SimAPI
    public Map setIgnoreSSL(boolean ignoreSSL) {
        XenSimulatorSettings.ignoreSSL = ignoreSSL;
        return Response.RESPONSE_OK;
    }

    @SimAPI
    public Map setKeystoreFile(String keystoreFile) {
        XenSimulatorSettings.keystoreFile = keystoreFile;
        return Response.RESPONSE_OK;
    }

    @SimAPI
    public Map setKeystorePass(char[] keystorePass) {
        XenSimulatorSettings.keystorePass = keystorePass;
        return Response.RESPONSE_OK;
    }

    @SimAPI
    public Map setGetResponseContent(String getResponseContent) {
        XenSimulatorSettings.getResponseContent = getResponseContent;
        return Response.RESPONSE_OK;
    }

    /**
     * list out all files named xxx.xml under the resources dir.
     *
     * @return return a list like "/host_all_records.xml" , ...
     */
    @SimAPI
    public Map getAllXmlFileNames() {
        return Response.newRsp().withValue(xmlRecordFiles).build();
    }

    @SimAPI
    public Map getXmlFileContent(String file) {
        Object valueObject = getXmlResponseValueForFile(file);
        if (valueObject == null) {
            return Response.newRsp().withError("The file is not found - " + file + ", all known files are:" + Arrays.toString(xmlRecordFiles)).build();
        }
        return Response.newRsp().withValue(valueObject).build();
    }

    /**
     * update the specified file content
     * @param file  the xml path file name
     * @param xmlpath  the xml path eg: $Value$OpaqueRef:144cc055-0ba7-f978-36c6-f63d4681e924$cpu_info$socket_count
     * @param updateObject the value which will be replaced.
     * @return
     */
    @SimAPI
    public Map updateXmlFileContent(String file, String xmlpath, Object updateObject) {
        return _updateXmlFileContent0(file, xmlpath, updateObject);
    }

    private Map _updateXmlFileContent0(String file, String xmlpath, Object updateObject) {
        XMLResponse xmlResponse = getXmlResponseForFile(file);
        if (xmlResponse == null) {
            return Response.newRsp().withError("The file is not found - " + file + ", all known files are:" + Arrays.toString(xmlRecordFiles)).build();
        }
        try {
            ObjectPathUtils.update(xmlResponse.result, xmlpath, updateObject);
            return Response.RESPONSE_OK;
        } catch (Exception e) {
            LOG.error("Fail to update the xml content", e);
            return Response.newRsp().withError(e.getMessage()).build();
        }
    }

    // we have to add those methods because
    @SimAPI
    public Map updateXmlFileContent(String file, String xmlpath, Integer updateObject) {
        return _updateXmlFileContent0(file, xmlpath, updateObject);
    }
    @SimAPI
    public Map updateXmlFileContent(String file, String xmlpath, Double updateObject) {
        return _updateXmlFileContent0(file, xmlpath, updateObject);
    }
    @SimAPI
    public Map updateXmlFileContent(String file, String xmlpath, Long updateObject) {
        return _updateXmlFileContent0(file, xmlpath, updateObject);
    }
    @SimAPI
    public Map updateXmlFileContent(String file, String xmlpath, Boolean updateObject) {
        return _updateXmlFileContent0(file, xmlpath, updateObject);
    }
    @SimAPI
    public Map updateXmlFileContent(String file, String xmlpath, String updateObject) {
        return _updateXmlFileContent0(file, xmlpath, updateObject);
    }
    @SimAPI
    public Map updateXmlFileContent(String file, String xmlpath, Map updateObject) {
        return _updateXmlFileContent0(file, xmlpath, updateObject);
    }
    @SimAPI
    public Map updateXmlFileContent(String file, String xmlpath, Object[] updateObject) {
        return _updateXmlFileContent0(file, xmlpath, updateObject);
    }

    public static Object getXmlResponseValueForFile(String file) {
        XMLResponse resp =  xml2Map.get(file);
        if (resp == null) {
            return null;
        }
        return resp.value;
    }

    public static XMLResponse getXmlResponseForFile(String file) {
        XMLResponse resp =  xml2Map.get(file);
        if (resp == null) {
            return null;
        }
        return resp;
    }


    @SimAPI
    public static Map setResponseDelayInMs(long responseDelayInMs) {
        XenSimulatorSettings.responseDelayInMs = responseDelayInMs;
        return Response.RESPONSE_OK;
    }

    @SimAPI
    public static Map regUriPattern2Response(String uriRegexPattern, String response) {
        try {
            Pattern.compile(uriRegexPattern);
            uriRegex2Response.put(uriRegexPattern, response);
            return Response.RESPONSE_OK;
        } catch (Exception e) {
            LOG.error("Invalid uri regex pattern {}", uriRegexPattern);
            return Response.newRsp().withError("Invalid regex pattern - " + uriRegexPattern).build();
        }
    }





    public interface  GetRequestHandler {
        String processGetRequest(String uri);
    }

    public static class DefaultGetRequestHandler implements GetRequestHandler {

        @Override
        public String processGetRequest(String uri) {
            Optional<Map.Entry<String, String>> optionalEntry = uriRegex2Response.entrySet().stream().filter(en -> Pattern.compile(en.getKey()).matcher(en.getValue()).matches()).findFirst();
            if (optionalEntry.isPresent()) {
                return optionalEntry.get().getValue();
            }
            else {
                LOG.info("No matched pattern found for uri {}, will return default content", uri);
                return XenSimulatorSettings.getResponseContent;
            }
        }
    }

}
