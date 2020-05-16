package org.ifaster.http.common.interceptor;

import okhttp3.Interceptor;
import org.ifaster.http.common.config.HttpClientConfig;

/**
 * @author yangnan
 */
public interface HttpInterceptor extends Interceptor {

    /**
     * Useful constant for the highest precedence value.
     * @see java.lang.Integer#MIN_VALUE
     */
    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    /**
     * 排序 越小越靠前
     * @return
     */
    default int order() {
        return HIGHEST_PRECEDENCE;
    }

    /**
     * 设置配置信息
     *
     * @param clientConfig
     */
    void setHttpClientConfig(HttpClientConfig clientConfig);
}
