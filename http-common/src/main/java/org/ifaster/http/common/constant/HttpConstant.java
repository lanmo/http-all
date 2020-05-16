package org.ifaster.http.common.constant;

/**
 * 常量池
 *
 * @author yangnan
 */
public class HttpConstant {

    public static final String DEFAULT_HEADER_VALUE = "";
    public static final String DEFAULT_REQUEST_ID = "X-REQUEST-ID";
    public static final String DEFAULT_ACCEPT_ENCODING = "Accept-Encoding";
    public static final String DEFAULT_ACCEPT_ENCODING_VALUE = "identity";
    public static final String DEFAULT_RETRY_TIMES = "X-RETRY-TIMES";
    public static final String DEFAULT_REQUEST_IGNORE = "X-REQUEST-IGNORE";
    public static final String DEFAULT_RESPONSE_IGNORE = "X-RESPONSE-IGNORE";
    /**配置文件前缀*/
    public static final String CONFIG_PREFIX = "http.client";
    /**当前系统标识*/
    public static final String HTTP_CLIENT_NAME = "X-CLIENT-NAME";
    /**json数据 utf-8*/
    public static final String MEDIA_TYPE_JSON = "application/json; charset=utf-8";
    /**json数据*/
    public static final String TYPE_JSON = "application/json";
    /**xml数据*/
    public static final String MEDIA_TYPE_XML = "application/xml; charset=utf-8";
    /**文件数据*/
    public static final String MEDIA_TYPE_FORM_DATA = "multipart/form-data";
    /**
     * 空字符串
     */
    public static final String EMPTY = "";

    /**
     * get请求中的问号
     */
    public static final String GET = "?";

    /**
     * utf-8 编码
     */
    public static final String UTF_8 = "UTF-8";

    /**
     * 客户端名称
     */
    public static final String DEFAULT_CLIENT_NAME = "defaultHttpClient";

    /**
     * 连接异常
     */
    public static final int CONNECTION_TIME_OUT = 503;

}
