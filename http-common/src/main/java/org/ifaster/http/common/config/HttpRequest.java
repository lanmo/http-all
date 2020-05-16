package org.ifaster.http.common.config;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author yangnan
 */
@Builder
@Data
public class HttpRequest {

    /**
     * 请求url
     */
    private String url;

    /**
     * 请求头
     */
    private Map<String, ?> headers;
    /**
     * 请求参数
     */
    private Map<String, ?> requestParams;

    /**
     * 请求body体 适用于post json和xml的数据
     */
    private String requestBody;

    /**
     * 重试次数 为空默认读取配置
     */
    private Integer retryTimes;

    /**
     * 记录请求日志 为空读取配置
     */
    private Boolean requestLog;

    /**
     * 记录返回值日志 为空读取配置
     */
    private Boolean responseLog;

    /**
     * 文件下载的路径
     */
    private String path;
}
