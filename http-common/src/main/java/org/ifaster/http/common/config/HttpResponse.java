package org.ifaster.http.common.config;

import lombok.Data;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author yangnan
 */
@Data
public class HttpResponse {

    private static final String ERROR_MSG = "response is null";
    private static final String ERROR_RESPONSE_MSG = "response body is null";

    private boolean isSuccessful;
    private String responseBody;
    private String errorMsg;
    private Throwable e;

    /**
     * 调用http接口成功
     *
     * @param responseBody
     * @return
     */
    public static HttpResponse buildSuccessResponse(String responseBody) {
        HttpResponse result = new HttpResponse();
        result.setSuccessful(true);
        result.setResponseBody(responseBody);
        return result;
    }

    /**
     * 调用异常
     *
     * @return
     */
    public static HttpResponse buildIOExceptionResponse(Throwable e) {
        HttpResponse result = new HttpResponse();
        result.setSuccessful(false);
        result.setE(e);
        result.setErrorMsg(e.getMessage());
        return result;
    }

    /**
     * 调用http接口异常
     *
     * @param execute
     * @return
     */
    public static HttpResponse buildErrorResponse(Response execute) {
        HttpResponse result = new HttpResponse();
        result.setSuccessful(false);
        if (execute == null) {
            result.setErrorMsg(ERROR_MSG);
            return result;
        }
        if (execute.body() != null) {
            result.setErrorMsg(execute.body().toString());
        } else {
            result.setErrorMsg(ERROR_RESPONSE_MSG);
        }
        return result;
    }
}
