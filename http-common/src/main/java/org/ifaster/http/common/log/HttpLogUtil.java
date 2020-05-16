package org.ifaster.http.common.log;

import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.ifaster.http.common.config.HttpClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.ifaster.http.common.constant.HttpConstant.CONNECTION_TIME_OUT;

/**
 * 日志工具类
 *
 * @author yangnan
 */
public class HttpLogUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger("HTTP_REQUEST_RESPONSE_LOG");

    private final static Logger LOG = LoggerFactory.getLogger(HttpLogUtil.class);

    /**
     * 记录请求参数
     *
     * @param request
     * @param start
     * @param clientConfig
     */
    public static void recordRequestLog(Request request, long start, HttpClientConfig clientConfig) {
        try {
            HttpRequestLog requestLog = new HttpRequestLog();
            requestLog.setRequestTime(start);
            requestLog.setMethod(request.method());
            requestLog.setClientName(clientConfig.getClientName());
            requestLog.setUrl(request.url().toString());
            Headers headers = request.headers();
            int length = headers.size();
            Map<String, String> headerMap = new HashMap<>(length);
            for (int i=0; i<length; i++) {
                headerMap.put(headers.name(i), headers.value(i));
            }
            requestLog.setRequestHeader(headerMap);

            Set<String> mdcKeys = clientConfig.getMdcKeys();
            if (mdcKeys != null && !mdcKeys.isEmpty()) {
                Map<String, String> mdcValue = new HashMap<>(mdcKeys.size());
                mdcKeys.forEach(key -> mdcValue.put(key, MDC.get(key)));
                requestLog.setMdcValue(mdcValue);
            }

            RequestBody body = request.body();
            if (body == null) {
                LOGGER.info(JSON.toJSONString(requestLog));
                return;
            }

            if (body.contentType() != null) {
                requestLog.setMediaType(body.contentType().toString());
                if (body.contentType().charset() != null) {
                    requestLog.setCharset(body.contentType().charset().displayName());
                }
            }
            if (body instanceof FormBody) {
                FormBody formBody = (FormBody) body;
                length = formBody.size();
                Map<String, String> formMap = new HashMap<>(length);
                for (int i=0; i<length; i++) {
                    formMap.put(formBody.name(i), formBody.value(i));
                }
                requestLog.setFormValue(formMap);
            }
            LOGGER.info(JSON.toJSONString(requestLog));
        } catch (Throwable e) {
            LOG.error("记录请求参数日志异常", e);
        }
    }

    /**
     * 打印返回值
     *
     * @param response
     * @param start
     * @param clientConfig
     */
    public static void recordResponseLog(Response response, long start, HttpClientConfig clientConfig) {
        HttpResponseLog responseLog = new HttpResponseLog();
        responseLog.setConsume(System.currentTimeMillis() - start);
        responseLog.setClientName(clientConfig.getClientName());
        if (response == null) {
            responseLog.setCode(CONNECTION_TIME_OUT);
            responseLog.setSuccess(false);
            LOGGER.info(JSON.toJSONString(responseLog));
            return;
        }
        try {
            responseLog.setSuccess(response.isSuccessful());
            responseLog.setCode(response.code());
            responseLog.setMessage(response.message());
            Headers headers = response.headers();
            int length = headers.size();
            Map<String, String> headerMap = new HashMap<>(length);
            for (int i=0; i<length; i++) {
                headerMap.put(headers.name(i), headers.value(i));
            }
            responseLog.setResponseHeader(headerMap);
            ResponseBody body = response.peekBody(1024 * 1024);
            if (body == null) {
                responseLog.setSuccess(false);
                LOGGER.info(JSON.toJSONString(responseLog));
                return;
            }
            responseLog.setResponseBody(body.string());
            LOGGER.info(JSON.toJSONString(responseLog));
        } catch (Throwable e) {
            LOG.error("记录返回值异常", e);
        }
    }
}
