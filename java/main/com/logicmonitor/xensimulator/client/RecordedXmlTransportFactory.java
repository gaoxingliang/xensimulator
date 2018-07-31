package com.logicmonitor.xensimulator.client;

import com.logicmonitor.xensimulator.utils.XmlPrettyFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.*;
import org.apache.xmlrpc.common.XmlRpcStreamRequestConfig;
import org.xml.sax.SAXException;

import java.io.*;

public class RecordedXmlTransportFactory extends XmlRpcCommonsTransportFactory {

    public static final Logger LOG = LogManager.getLogger();

    /**
     * Creates a new instance.
     *
     * @param pClient The client, which is controlling the factory.
     */
    public RecordedXmlTransportFactory(XmlRpcClient pClient) {
        super(pClient);
    }

    @Override
    public XmlRpcTransport getTransport() {
        return new RecordedRpcTransport(this);
    }

    class RecordedRpcTransport extends XmlRpcCommonsTransport {

        /**
         * Creates a new instance.
         *
         * @param pFactory The factory, which created this transport.
         */
        public RecordedRpcTransport(XmlRpcCommonsTransportFactory pFactory) {
            super(pFactory);
        }

        /**
         * Dumps outgoing XML-RPC requests to the log
         */
        @Override
        protected void writeRequest(final XmlRpcStreamTransport.ReqWriter pWriter) throws XmlRpcException {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                pWriter.write(baos);
            }
            catch (SAXException e) {
            }
            catch (IOException e) {
            }
            LOG.debug("Send request=\n{}", XmlPrettyFormatter.toPrettyString(baos.toString()));
            super.writeRequest(pWriter);
        }


        /**
         * Dumps incoming XML-RPC responses to the log
         */
        @Override
        protected Object readResponse(XmlRpcStreamRequestConfig pConfig, InputStream pStream) throws XmlRpcException {
            final StringBuffer sb = new StringBuffer();

            try {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(pStream));
                String line = reader.readLine();
                while (line != null) {
                    sb.append(line);
                    line = reader.readLine();
                }
            }
            catch (final IOException e) {
                LOG.error("Fail to dump", e);
            }

            LOG.debug("Receive response=\n{}", XmlPrettyFormatter.toPrettyString(sb.toString()));

            final ByteArrayInputStream bais = new ByteArrayInputStream(sb.toString().getBytes());
            return super.readResponse(pConfig, bais);
        }


    }
}
