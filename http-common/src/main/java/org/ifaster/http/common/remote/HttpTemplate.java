package org.ifaster.http.common.remote;

import org.ifaster.http.common.callback.HttpCallback;
import org.ifaster.http.common.config.HttpRequest;
import org.ifaster.http.common.config.HttpResponse;

import java.util.Map;

/**
 * @author yangnan
 */
public interface HttpTemplate {

    /**
     * 初始化
     *
     */
    void init();

    /**
     * 是否初始化成功 true表示成功 false表示失败
     *
     * @return
     */
    boolean isStart();

    /**
     * get请求
     *
     * @param request 请求体
     * @param tags tag标签
     * @return
     */
    HttpResponse get(HttpRequest request, String... tags);

    /**
     * get请求
     *
     * @param url 请求utl
     * @param headerParams http的header
     * @param tags   tag
     * @return
     */
    HttpResponse get(String url, Map<String, ?> headerParams, String... tags);

    /**
     * get请求
     *
     * @param url 请求utl
     * @param tags   tag
     * @return
     */
    HttpResponse get(String url, String... tags);

    /**
     * get请求
     *
     * @param url 请求url
     * @param headerParams http的header
     * @param tags    tag
     * @param requestParams 请求参数
     * @return
     */
    HttpResponse get(String url, Map<String, ?> headerParams, Map<String, ?> requestParams, String... tags);

    /**
     * 下载文件 get请求
     *
     * @param request
     */
    void downloadGet(HttpRequest request, String... tags);

    /**
     * 下载文件 post请求
     *
     * @param request
     */
    void downloadPost(HttpRequest request, String... tags);

    /**
     * 下载文件 postJson请求
     *
     * @param request
     */
    void downloadPostJson(HttpRequest request, String... tags);

    /**
     * 下载文件 postJson请求
     *
     * @param request
     * @param tags
     * @param contentType
     */
    void downloadPostBody(HttpRequest request, String contentType, String... tags);

    /**
     * 下载文件 postXml请求
     *
     * @param request
     */
    void downloadPostXml(HttpRequest request, String... tags);


    /**
     * post请求
     *
     * @param request
     * @param tags
     * @return
     */
    HttpResponse post(HttpRequest request, String... tags);

    /**
     * post json数据
     *
     * @param request
     * @param tags
     * @return
     */
    HttpResponse postJson(HttpRequest request, String... tags);

    /**
     * post body数据
     *
     * @param request
     * @param tags
     * @param contentType
     * @return
     */
    HttpResponse postBody(HttpRequest request, String contentType, String... tags);

    /**
     * post xml数据
     *
     * @param request
     * @param tags
     * @return
     */
    HttpResponse postXml(HttpRequest request, String... tags);

    /**
     * 异步GET请求
     *
     * @param request
     * @param callback
     */
    void getAsync(HttpRequest request, HttpCallback callback, String... tags);

    /**
     * 异步post请求
     *
     * @param request 请求参数
     * @param callback      回调方法
     */
    void postAsync(HttpRequest request, HttpCallback callback, String... tags);

    /**
     * 异步post json请求
     *
     * @param request 请求参数
     * @param callback      回调方法
     * @param tags
     */
    void postJsonAsync(HttpRequest request, HttpCallback callback, String... tags);

    /**
     * 异步post xml请求
     *
     * @param request 请求参数
     * @param callback      回调方法
     * @param tags
     */
    void postXmlAsync(HttpRequest request, HttpCallback callback, String... tags);

    /**
     * 异步post xml请求
     *
     * @param request 请求参数
     * @param callback      回调方法
     * @param contentType
     * @param tags
     */
    void postBodyAsync(HttpRequest request, HttpCallback callback, String contentType, String... tags);

    /**
     * 同步post请求
     *
     * @param url          请求地址
     * @param headerParams 请求头参数
     * @param formParams   请求参数
     * @param tags
     * @return 超时未返回，会返回错误提示
     */
     HttpResponse post(String url, Map<String, ?> headerParams, Map<String, ?> formParams, String... tags);

    /**
     * 同步post json请求
     *
     * @param url          请求地址
     * @param headerParams 请求头参数
     * @param jsonString   请求参数
     * @return 超时未返回，会返回错误提示
     */
    HttpResponse postJson(String url, Map<String, ?> headerParams, String jsonString, String... tag);

    /**
     * 上传文件
     *
     * @param url          请求地址
     * @param headerParams 请求头参数
     * @param tag tag
     * @param formParams   请求参数
     * @return 超时未返回，会返回null
     */
    HttpResponse postMultipart(String url, Map<String, ?> headerParams, Map<String, ?> formParams, String... tag);

    /**
     * 上传文件
     *
     * @param request 请求头参数
     * @param tags
     * @return 超时未返回，会返回null
     */
    HttpResponse postMultipart(HttpRequest request, String... tags);

    /**
     * 同步post xml请求
     *
     * @param url          请求地址
     * @param headerParams 请求头参数
     * @param xmlString    请求参数
     * @param tags
     * @return 超时未返回，会返回错误提示
     */
    HttpResponse postXml(String url, Map<String, ?> headerParams, String xmlString, String... tags);


    /**
     * 异步GET请求
     *
     * @param url
     * @param callback
     */
    void getAsync(String url, HttpCallback callback, Map<String, ?> headerParams, Map<String, ?> getParams, String... tag);

    /**
     * 异步post请求
     *
     * @param url          请求地址
     * @param headerParams 请求头参数
     * @param formParams   请求参数
     * @param callback     回调方法
     */
    void postAsync(String url, Map<String, ?> headerParams, Map<String, ?> formParams, HttpCallback callback, String... tag);

    /**
     * 异步post json请求
     *
     * @param url          请求地址
     * @param headerParams 请求头参数
     * @param jsonString   请求参数
     * @param callback     回调方法
     * @param tags
     */
    void postJsonAsync(String url, Map<String, ?> headerParams, String jsonString, HttpCallback callback, String... tags);
}
