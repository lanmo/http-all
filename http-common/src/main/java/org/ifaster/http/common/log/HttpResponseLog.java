package org.ifaster.http.common.log;

import lombok.Data;

import java.util.Map;

/**
 * 响应值
 *
 * @author yangnan
 */
@Data
public class HttpResponseLog {
    private String clientName;
    private Map<String, ?> responseHeader;
    private String responseBody;
    private int code;
    private boolean success;
    private long consume;
    private String message;
}
