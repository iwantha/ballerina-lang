package org.ballerinalang.net.http;

import org.ballerinalang.connector.api.BallerinaConnectorException;
import org.ballerinalang.connector.api.ConnectorFutureListener;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.services.ErrorHandlerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.transport.http.netty.message.HTTPCarbonMessage;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * {@code HttpConnectorFutureListener} is the responsible for acting on notifications received from Ballerina side.
 *
 * @since 0.94
 */
public class HttpConnectorFutureListener implements ConnectorFutureListener {
    private static final Logger log = LoggerFactory.getLogger(HttpConnectorFutureListener.class);
    private HTTPCarbonMessage requestMessage;

    public HttpConnectorFutureListener(HTTPCarbonMessage requestMessage) {
        this.requestMessage = requestMessage;
    }

    @Override
    public void notifySuccess() {
        //nothing to do, this will get invoked once resource finished execution
    }

    @Override
    public void notifyReply(BValue response) {
        HTTPCarbonMessage responseMessage = (HTTPCarbonMessage) ((BStruct) response)
                .getNativeData(org.ballerinalang.net.http.Constants.TRANSPORT_MESSAGE);

        HttpUtil.handleResponse(requestMessage, responseMessage);
    }

    @Override
    public void notifyFailure(BallerinaConnectorException ex) {
        Object carbonStatusCode = requestMessage.getProperty(Constants.HTTP_STATUS_CODE);
        int statusCode = (carbonStatusCode == null) ? 500 : Integer.parseInt(carbonStatusCode.toString());
        String errorMsg = ex.getMessage();
        log.error(errorMsg);
        ErrorHandlerUtils.printError(ex);
        if (statusCode == 404) {
            HttpUtil.handleResponse(requestMessage, createErrorMessage(errorMsg, statusCode));
        } else {
            // TODO If you put just "", then we got a NPE. Need to find why
            HttpUtil.handleResponse(requestMessage, createErrorMessage("  ", statusCode));
        }
    }

    private HTTPCarbonMessage createErrorMessage(String payload, int statusCode) {

        HTTPCarbonMessage response = new HTTPCarbonMessage();

        response.addMessageBody(ByteBuffer.wrap(payload.getBytes(Charset.defaultCharset())));
        response.setEndOfMsgAdded(true);
        byte[] errorMessageBytes = payload.getBytes(Charset.defaultCharset());

        // TODO: Set following according to the request
        Map<String, String> transportHeaders = new HashMap<>();
        transportHeaders.put(org.wso2.carbon.transport.http.netty.common.Constants.HTTP_CONNECTION,
                org.wso2.carbon.transport.http.netty.common.Constants.CONNECTION_KEEP_ALIVE);
        transportHeaders.put(org.wso2.carbon.transport.http.netty.common.Constants.HTTP_CONTENT_TYPE,
                org.wso2.carbon.transport.http.netty.common.Constants.TEXT_PLAIN);
        transportHeaders.put(org.wso2.carbon.transport.http.netty.common.Constants.HTTP_CONTENT_LENGTH,
                (String.valueOf(errorMessageBytes.length)));

        response.setHeaders(transportHeaders);

        response.setProperty(org.wso2.carbon.transport.http.netty.common.Constants.HTTP_STATUS_CODE, statusCode);
        response.setProperty(org.wso2.carbon.messaging.Constants.DIRECTION,
                org.wso2.carbon.messaging.Constants.DIRECTION_RESPONSE);
        return response;

    }
}
