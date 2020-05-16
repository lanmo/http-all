package org.ifaster.http.common.config;

import lombok.Data;
import org.ifaster.http.common.enums.ClientType;

import java.util.Set;

import static org.ifaster.http.common.constant.HttpConstant.DEFAULT_CLIENT_NAME;

/**
 * @author yangnan
 */
@Data
public class HttpClientConfig {

    /**最大空闲连接数*/
    private int maxIdleConnections = 300;
    /**长连接时长(单位ms) */
    private long keepAliveDuration = 5 * 60 * 1000;
    /**连接时间超时时间(单位ms) */
    private long connectTimeout = 3000;
    /**读取socket超时时间(单位ms) */
    private long readTimeout = 3000;
    /**发送socket超时时间(单位ms) */
    private long writeTimeout = 3000;
    /**客户端名称*/
    private String clientName = DEFAULT_CLIENT_NAME;
    /**重试次数 0表示不重试 1表示重试1次 总共会发起两次请求*/
    private int maxRetry = 0;
    /**记录请求日志 true表示记录 false表示不记录*/
    private boolean requestLog = true;
    /**记录响应日志 true表示记录 false表示不记录*/
    private boolean responseLog = true;
    /**连接重试 true表示重试 false表示不重试 和 maxRetry没关系 默认true 重试次数20 参考{@link okhttp3.internal.http.RetryAndFollowUpInterceptor}*/
    private boolean retryOnConnectionFailure = true;

    /**
     * 时间跟踪 默认不跟踪
     */
    private boolean traceTime = false;

    /**
     * MDC 中对应的key值 只日志记录使用
     */
    private Set<String> mdcKeys;

    /**
     * 实例名字
     */
    private String instanceName;

    /**
     * 异步请求有用最大请求数 默认64
     */
    private int maxRequests = 64;
    /**
     * 每个host最大请求数 默认5
     * 异步请求有用
     */
    private int maxRequestsPerHost = 5;

    /**
     * 客户端类型
     */
    private ClientType clientType = ClientType.OK_HTTP;
}
