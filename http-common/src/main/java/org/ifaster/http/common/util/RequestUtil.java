package org.ifaster.http.common.util;

import okhttp3.Headers;

/**
 * @author yangnan
 */
public class RequestUtil {

    /**
     * 获取int value
     *
     * @param headers
     * @param headerName
     * @return
     */
    public static int getIntValue(Headers headers, String headerName, int defaultValue) {
        String value = getHeader(headers, headerName);
        return (value == null || value.isEmpty()) ? defaultValue : Integer.parseInt(value);
    }

    /**
     * 获取http header中的属性值
     *
     * @param headers
     * @param headerName
     * @return
     */
    public static String getHeader(Headers headers, String headerName) {
        if (headers == null) {
            return null;
        }
        return headers.get(headerName);
    }

    /**
     * 获取http header中的属性值
     *
     * @param headers
     * @param headerName
     * @param defaultValue
     * @return
     */
    public static String getHeader(Headers headers, String headerName, String defaultValue) {
        String value = getHeader(headers, headerName);
        return value == null ? defaultValue : value;
    }

    /**
     * 获取boolean 值
     *
     * @param headerName
     * @param headers
     * @param headerName
     * @param defaultValue
     * @return
     */
    public static boolean getBooleanValue(Headers headers, String headerName, boolean defaultValue) {
        String value = getHeader(headers, headerName);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }
}
