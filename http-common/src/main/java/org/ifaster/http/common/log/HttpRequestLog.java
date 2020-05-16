package org.ifaster.http.common.log;

import lombok.Data;

import java.util.Map;

/**
 * 记录请求参数和返回值
 *
 * @author yangnan
 */
@Data
public class HttpRequestLog {
    private Map<String, ?> requestHeader;
    private Map<String, ?> formValue;
    private String clientName;
    private Map<String, ?> mdcValue;
    private String content;
    private String method;
    private Long requestTime;
    private String url;
    private String mediaType;
    private String charset;
}
