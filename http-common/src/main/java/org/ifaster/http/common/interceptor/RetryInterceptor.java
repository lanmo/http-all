package org.ifaster.http.common.interceptor;

import okhttp3.Response;
import okhttp3.internal.http.RealInterceptorChain;
import org.ifaster.http.common.config.HttpClientConfig;

import java.io.IOException;

import static org.ifaster.http.common.constant.HttpConstant.DEFAULT_RETRY_TIMES;
import static org.ifaster.http.common.util.RequestUtil.getIntValue;

/**
 * 重试拦截器
 *
 * @author yangnan
 */
public class RetryInterceptor extends AbstractInterceptor {

    private HttpClientConfig clientConfig;

    @Override
    public Response intercept(Chain chain) throws IOException {
        int retryTimes = 0;
        int customerRetryTimes = getIntValue(chain.request().headers(), DEFAULT_RETRY_TIMES, clientConfig.getMaxRetry());
        Response response = doRequest(chain);
        while (response == null && retryTimes++ < customerRetryTimes) {
            response = doRequest(chain);
        }
        return response;
    }

    /**
     * 发送请求
     *
     * @param chain
     * @return
     */
    private Response doRequest(Chain chain) {
        Response response = null;
        try {
            response = chain.proceed(chain.request());
        } catch (Throwable e) {
            if (chain instanceof RealInterceptorChain) {
                IOException ioException = e instanceof IOException ? (IOException) e : new IOException(e);
                ((RealInterceptorChain) chain).eventListener().callFailed(chain.call(), ioException);
            }
            logger.error("请求[{}]异常", chain.request(), e);
        }
        return response;
    }

    @Override
    public int order() {
        return 5;
    }

    @Override
    public void setHttpClientConfig(HttpClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }
}
