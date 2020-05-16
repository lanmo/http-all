package org.ifaster.http.common.interceptor;

import okhttp3.Request;
import okhttp3.Response;
import org.ifaster.http.common.config.HttpClientConfig;
import org.ifaster.http.common.log.HttpLogUtil;

import java.io.IOException;

import static org.ifaster.http.common.constant.HttpConstant.DEFAULT_REQUEST_IGNORE;
import static org.ifaster.http.common.constant.HttpConstant.DEFAULT_RESPONSE_IGNORE;
import static org.ifaster.http.common.util.RequestUtil.getBooleanValue;

/**
 * 打印请求参数与返回值
 *
 * @author yangnan
 */
public class LogTraceInterceptor extends AbstractInterceptor {

    private HttpClientConfig clientConfig;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        boolean requestLogSwitch = getBooleanValue(request.headers(), DEFAULT_REQUEST_IGNORE, clientConfig.isRequestLog());
        long start = System.currentTimeMillis();
        if (requestLogSwitch) {
            HttpLogUtil.recordRequestLog(request, start, clientConfig);
        }
        Response response = null;
        try {
            response = chain.proceed(request);
        } finally {
            boolean responseLogSwitch = getBooleanValue(request.headers(), DEFAULT_RESPONSE_IGNORE, clientConfig.isResponseLog());
            if (responseLogSwitch) {
                HttpLogUtil.recordResponseLog(response, start, clientConfig);
            }
        }
        return response;
    }

    @Override
    public void setHttpClientConfig(HttpClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }
}
