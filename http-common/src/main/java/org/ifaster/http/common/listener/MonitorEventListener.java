package org.ifaster.http.common.listener;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;

/**
 * @author yangnan
 */
public class MonitorEventListener extends EventListener {

    private static Logger LOGGER = LoggerFactory.getLogger("HTTP_TRACE_TIME");

    private StringBuffer builder;
    private String url;

    /**
     * 每次请求的开始时间，单位纳秒
     */
    private long callStartNanos;

    public MonitorEventListener(String url, String clientName) {
        this.callStartNanos = System.nanoTime();
        this.url = url;
        builder = new StringBuffer( 400);
        builder.append("clientName=").append(clientName).append("|");
    }

    @Override
    public void callStart(Call call) {
        builder.append("callStart=").append(mill()).append("|");
        super.callStart(call);
    }

    @Override
    public void dnsStart(Call call, String domainName) {
        builder.append("dnsStart=").append(mill()).append("|");
        super.dnsStart(call, domainName);
    }

    @Override
    public void dnsEnd(Call call, String domainName, @Nullable List<InetAddress> inetAddressList) {
        builder.append("dnsEnd=").append(mill()).append("|");
        super.dnsEnd(call, domainName, inetAddressList);
    }

    @Override
    public void connectStart(Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
        builder.append("connectStart=").append(mill()).append("|");
        super.connectStart(call, inetSocketAddress, proxy);
    }

    @Override
    public void secureConnectStart(Call call) {
        builder.append("secureConnectStart=").append(mill()).append("|");
        super.secureConnectStart(call);
    }

    @Override
    public void secureConnectEnd(Call call, @Nullable Handshake handshake) {
        builder.append("secureConnectEnd=").append(mill()).append("|");
        super.secureConnectEnd(call, handshake);
    }

    @Override
    public void connectEnd(Call call, InetSocketAddress inetSocketAddress, @Nullable Proxy proxy, @Nullable Protocol protocol) {
        builder.append("connectEnd=").append(mill()).append("|");
        super.connectEnd(call, inetSocketAddress, proxy, protocol);
    }

    @Override
    public void connectFailed(Call call, InetSocketAddress inetSocketAddress, @Nullable Proxy proxy, @Nullable Protocol protocol, @Nullable IOException ioe) {
        builder.append("connectFailed=").append(mill()).append("|");
        super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe);
    }

    @Override
    public void connectionAcquired(Call call, Connection connection) {
        builder.append("connectionAcquired=").append(mill()).append("|");
        super.connectionAcquired(call, connection);
    }

    @Override
    public void connectionReleased(Call call, Connection connection) {
        builder.append("connectionReleased=").append(mill()).append("|");
        super.connectionReleased(call, connection);
    }

    @Override
    public void requestHeadersStart(Call call) {
        builder.append("requestHeadersStart=").append(mill()).append("|");
        super.requestHeadersStart(call);
    }

    @Override
    public void requestHeadersEnd(Call call, Request request) {
        builder.append("requestHeadersEnd=").append(mill()).append("|");
        super.requestHeadersEnd(call, request);
    }

    @Override
    public void requestBodyStart(Call call) {
        builder.append("requestBodyStart=").append(mill()).append("|");
        super.requestBodyStart(call);
    }

    @Override
    public void requestBodyEnd(Call call, long byteCount) {
        builder.append("requestBodyEnd=").append(mill()).append("|");
        super.requestBodyEnd(call, byteCount);
    }

    @Override
    public void responseHeadersStart(Call call) {
        builder.append("responseHeadersStart=").append(mill()).append("|");
        super.responseHeadersStart(call);
    }

    @Override
    public void responseHeadersEnd(Call call, Response response) {
        builder.append("responseHeadersEnd=").append(mill()).append("|");
        super.responseHeadersEnd(call, response);
    }

    @Override
    public void responseBodyStart(Call call) {
        builder.append("responseBodyStart=").append(mill()).append("|");
        super.responseBodyStart(call);
    }

    @Override
    public void responseBodyEnd(Call call, long byteCount) {
        builder.append("responseBodyEnd=").append(mill()).append("|");
        super.responseBodyEnd(call, byteCount);
    }

    @Override
    public void callEnd(Call call) {
        builder.append("callEnd=").append(mill()).append("|");
        super.callEnd(call);
        LOGGER.info("{}|{}", url, builder.toString());
    }

    @Override
    public void callFailed(Call call, IOException ioe) {
        builder.append("callFailed=").append(mill()).append("|");
        super.callFailed(call, ioe);
        LOGGER.info("{}|{}", url, builder.toString());
    }

    /**
     * 获取毫秒
     * @return
     */
    private double mill() {
        return (System.nanoTime() - callStartNanos) / 1000000.0;
    }
}
