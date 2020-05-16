package org.ifaster.http.common.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ok http 拦截器
 *
 * @author yangnan
 */
public abstract class AbstractInterceptor implements HttpInterceptor {
    protected Logger logger = LoggerFactory.getLogger(getClass());

}
