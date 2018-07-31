package com.logicmonitor.xensimulator.utils;

import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.common.TypeFactoryImpl;
import org.apache.xmlrpc.parser.XmlRpcResponseParser;
import org.apache.xmlrpc.util.SAXParsers;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.util.Map;

/**
 * this class could help to parse the xml response to a map
 * which we can simulate the result if needed.
 */
public class XMLResponse {

    public final int errorCode;
    public final String errorMessage;
    public Throwable errorCause;
    public final Map result; // the whole result
    public final Object value; // the value part
    private XMLResponse(int errorCode, String errorMessage, Throwable errorCause, Map result) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorCause = errorCause;
        this.result = result;
        if (result == null) {
            value = null;
        }
        else {
            value = result.get("Value");
        }
    }

    public static XMLResponse parse(InputStream inputStream) throws Exception {
        final XmlRpcResponseParser parser = new XmlRpcResponseParser(new XmlRpcClientConfigImpl(), new TypeFactoryImpl(null));
        final XMLReader xr = SAXParsers.newXMLReader();
        xr.setContentHandler(parser);
        xr.parse(new InputSource(inputStream));
        return new XMLResponse(parser.getErrorCode(), parser.getErrorMessage(), parser.getErrorCause(), (Map)parser.getResult());
    }

    @Override
    public String toString() {
        return "XMLResponse{" +
                "errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", errorCause=" + errorCause +
                ", result=" + result +
                ", value=" + value +
                '}';
    }
}
