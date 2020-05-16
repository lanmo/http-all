package org.ifaster.http.common.remote;

import okhttp3.*;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;
import org.ifaster.http.common.callback.HttpCallback;
import org.ifaster.http.common.config.HttpClientConfig;
import org.ifaster.http.common.config.HttpRequest;
import org.ifaster.http.common.config.HttpResponse;
import org.ifaster.http.common.interceptor.HttpInterceptor;
import org.ifaster.http.common.interceptor.RetryInterceptor;
import org.ifaster.http.common.listener.EventFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.net.URLEncoder;
import java.sql.Driver;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.ifaster.http.common.constant.HttpConstant.*;

/**
 * @author yangnan
 */
public class OkHttpTemplate implements HttpTemplate {
    private static Logger LOGGER = LoggerFactory.getLogger(OkHttpTemplate.class);

    private HttpClientConfig clientConfig;
    private OkHttpClient okHttpClient;

    private AtomicBoolean init = new AtomicBoolean(false);

    public OkHttpTemplate(HttpClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    @Override
    public void init() {
        if (!init.compareAndSet(false, true)) {
            return;
        }
        Assert.isTrue(StringUtils.hasLength(clientConfig.getInstanceName()), "instanceName required");
        LOGGER.info("HttpClientConfig配置[{}] OkHttpTemplate:[{}]", clientConfig, this);
        ConnectionPool connectionPool = new ConnectionPool(clientConfig.getMaxIdleConnections(), clientConfig.getKeepAliveDuration(), TimeUnit.SECONDS);
        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectionPool(connectionPool)
                .retryOnConnectionFailure(clientConfig.isRetryOnConnectionFailure())
                .connectTimeout(clientConfig.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(clientConfig.getWriteTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(clientConfig.getReadTimeout(), TimeUnit.MILLISECONDS)
        ;
        builder = addInterceptor(builder);
        if (clientConfig.isTraceTime()) {
            builder = builder.eventListenerFactory(new EventFactory(clientConfig.getClientName()));
        }
        okHttpClient = builder.build();
        if (clientConfig.getMaxRequests() >= 1) {
            okHttpClient.dispatcher().setMaxRequests(clientConfig.getMaxRequests());
        }
        if (clientConfig.getMaxRequestsPerHost() >= 1) {
            okHttpClient.dispatcher().setMaxRequestsPerHost(clientConfig.getMaxRequestsPerHost());
        }
    }

    /**
     * 添加拦截器
     *
     * @param builder
     * @return
     */
    private OkHttpClient.Builder addInterceptor(OkHttpClient.Builder builder) {
        ServiceLoader<HttpInterceptor> serviceLoader = ServiceLoader.load(HttpInterceptor.class);
        Iterator<HttpInterceptor> iterator = serviceLoader.iterator();
        List<HttpInterceptor> interceptors = new ArrayList<>();
        while (iterator.hasNext()) {
            HttpInterceptor interceptor = iterator.next();
            if (interceptor != null) {
                interceptor.setHttpClientConfig(clientConfig);
                interceptors.add(interceptor);
            }
        }
        interceptors.sort(Comparator.comparingInt(HttpInterceptor::order));
        interceptors.forEach(builder::addInterceptor);
        return builder;
    }

    @Override
    public boolean isStart() {
        return init.get();
    }

    @Override
    public HttpResponse get(HttpRequest request, String... tags) {
        return parseResponse(buildGetRequest(request, tags));
    }

    @Override
    public HttpResponse get(String url, Map<String, ?> headerParams, String... tags) {
        return get(url, headerParams, null, tags);
    }

    @Override
    public HttpResponse get(String url, String... tags) {
        return get(url, null, null, tags);
    }

    @Override
    public HttpResponse get(String url, Map<String, ?> headerParams, Map<String, ?> requestParams, String... tags) {
        return get(HttpRequest.builder().url(url).requestParams(requestParams).headers(headerParams).build(), tags);
    }

    @Override
    public void downloadGet(HttpRequest request, String... tags) {
        parseDownResponse(buildGetRequest(request, tags), request);
    }

    @Override
    public void downloadPost(HttpRequest request, String... tags) {
        parseDownResponse(buildPostRequest(request), request);
    }

    @Override
    public void downloadPostJson(HttpRequest request, String... tags) {
        downloadPostBody(request, MEDIA_TYPE_JSON, tags);
    }

    @Override
    public void downloadPostXml(HttpRequest request, String... tags) {
        downloadPostBody(request, MEDIA_TYPE_XML, tags);
    }

    @Override
    public void downloadPostBody(HttpRequest request, String contentType, String... tags) {
        parseDownResponse(buildPostBodyRequest(request, contentType, tags), request);
    }

    @Override
    public HttpResponse post(HttpRequest request, String... tags) {
        return parseResponse(buildPostRequest(request, tags));
    }

    @Override
    public HttpResponse postJson(HttpRequest request, String... tags) {
        return postBody(request, MEDIA_TYPE_JSON, tags);
    }

    @Override
    public HttpResponse postBody(HttpRequest request, String contentType, String... tags) {
        return parseResponse(buildPostBodyRequest(request, contentType, tags));
    }

    @Override
    public void getAsync(HttpRequest request, HttpCallback callback, String... tags) {
        okHttpClient.newCall(buildGetRequest(request, tags)).enqueue(callback);
    }

    @Override
    public void postAsync(HttpRequest request, HttpCallback callback, String... tags) {
        okHttpClient.newCall(buildPostRequest(request, tags)).enqueue(callback);
    }

    @Override
    public void postJsonAsync(HttpRequest request, HttpCallback callback, String... tags) {
        postBodyAsync(request, callback, MEDIA_TYPE_JSON, tags);
    }

    @Override
    public void postXmlAsync(HttpRequest request, HttpCallback callback, String... tags) {
        postBodyAsync(request, callback, MEDIA_TYPE_XML, tags);
    }

    @Override
    public void postBodyAsync(HttpRequest request, HttpCallback callback, String contentType, String... tags) {
        okHttpClient.newCall(buildPostBodyRequest(request, contentType, tags)).enqueue(callback);
    }

    @Override
    public HttpResponse post(String url, Map<String, ?> headerParams, Map<String, ?> formParams, String... tags) {
        return post(HttpRequest.builder().url(url).headers(headerParams).requestParams(formParams).build(), tags);
    }

    @Override
    public HttpResponse postJson(String url, Map<String, ?> headerParams, String jsonString, String... tags) {
        HttpRequest request = HttpRequest.builder().url(url).headers(headerParams).requestBody(jsonString).build();
        return postJson(request, tags);
    }

    @Override
    public HttpResponse postMultipart(String url, Map<String, ?> headerParams, Map<String, ?> formParams, String... tag) {
        return postMultipart(HttpRequest.builder().url(url).headers(headerParams).requestParams(formParams).build(), tag);
    }

    @Override
    public HttpResponse postMultipart(HttpRequest request, String... tags) {
        Headers.Builder headerBuilder = buildHeader(request);
        //3.x版本post请求换成FormBody 封装键值对参数
        MultipartBody.Builder multipartBody = new MultipartBody.Builder();
        Map<String, ?> requestParams = request.getRequestParams();
        if (requestParams != null) {
            //遍历集合
            requestParams.forEach((k, v) -> {
                if (v instanceof File) {
                    File uploadFile = (File) v;
                    multipartBody.addFormDataPart(k, uploadFile.getName(), RequestBody.create(MediaType.parse(MEDIA_TYPE_FORM_DATA), uploadFile));
                    return;
                }
                multipartBody.addFormDataPart(k, v == null ? "" : v.toString());
            });
        }
        String tag = convertTag(tags);
        //创建Request
        Request r = new Request.Builder().headers(headerBuilder.build()).url(request.getUrl()).post(multipartBody.build()).tag(tag).build();
        return parseResponse(r);
    }

    @Override
    public HttpResponse postXml(HttpRequest request, String... tags) {
        return postBody(request, MEDIA_TYPE_XML, tags);
    }

    @Override
    public HttpResponse postXml(String url, Map<String, ?> headerParams, String xmlString, String... tags) {
        HttpRequest request = HttpRequest.builder().url(url).headers(headerParams).requestBody(xmlString).build();
        return postXml(request, tags);
    }

    @Override
    public void getAsync(String url, HttpCallback callback, Map<String, ?> headerParams, Map<String, ?> getParams, String... tag) {
        getAsync(HttpRequest.builder().url(url).headers(headerParams).requestParams(getParams).build(), callback, tag);
    }

    @Override
    public void postAsync(String url, Map<String, ?> headerParams, Map<String, ?> formParams, HttpCallback callback, String... tag) {
        postAsync(HttpRequest.builder().url(url).headers(headerParams).requestParams(formParams).build(), callback, tag);
    }

    @Override
    public void postJsonAsync(String url, Map<String, ?> headerParams, String jsonString, HttpCallback callback, String... tags) {
        postBodyAsync(HttpRequest.builder().url(url).headers(headerParams).requestBody(jsonString).build(), callback, MEDIA_TYPE_JSON, tags);
    }

    /**
     * 请求头处理
     *
     * @param request
     */
    private Headers.Builder buildHeader(HttpRequest request) {
        Headers.Builder headerBuilder = new Headers.Builder();
        int retryTimes = request.getRetryTimes() == null ? clientConfig.getMaxRetry() : request.getRetryTimes();
        headerBuilder.add(DEFAULT_RETRY_TIMES, String.valueOf(retryTimes));
        boolean requestLog = request.getRequestLog() == null ? clientConfig.isRequestLog() : request.getRequestLog();
        headerBuilder.add(DEFAULT_REQUEST_IGNORE, Boolean.toString(requestLog));
        boolean responseLog = request.getResponseLog() == null ? clientConfig.isResponseLog() : request.getResponseLog();
        headerBuilder.add(DEFAULT_RESPONSE_IGNORE, Boolean.toString(responseLog));
        headerBuilder.add(DEFAULT_REQUEST_ID, UUID.randomUUID().toString().replace("-", ""));
        headerBuilder.add(DEFAULT_ACCEPT_ENCODING, DEFAULT_ACCEPT_ENCODING_VALUE);
        headerBuilder.add(HTTP_CLIENT_NAME, clientConfig.getInstanceName());
        Map<String, ?> headers = request.getHeaders();
        if (headers != null) {
            headers.forEach((k, v) -> headerBuilder.add(k, v == null ? DEFAULT_HEADER_VALUE : v.toString()));
        }
        return headerBuilder;
    }

    /**
     * tag转换
     *
     * @param tags
     */
    private String convertTag(String... tags) {
        if (tags == null || tags.length <= 0) {
            return "";
        }
        return tags[0];
    }

    /**
     * 解析响应结果
     *
     * @param request
     * @return
     */
    private HttpResponse parseResponse(Request request) {
        Response execute = null;
        try {
            execute = okHttpClient.newCall(request).execute();
            if (execute != null && execute.isSuccessful()) {
                return HttpResponse.buildSuccessResponse(execute.body().string());
            }
            return HttpResponse.buildErrorResponse(execute);
        } catch (Throwable e) {
            LOGGER.error("url:[{}]", request.url(), e);
            if (execute != null && execute.body() != null) {
                execute.body().close();
            }
            return HttpResponse.buildIOExceptionResponse(e);
        }
    }

    /**
     * 组建get url
     *
     * @param request
     * @return
     */
    private String buildGetUrl(HttpRequest request) {
        Map<String, ?> requestParams = request.getRequestParams();
        if (requestParams == null) {
            return request.getUrl();
        }
        StringBuilder builder = new StringBuilder(request.getUrl());
        if (builder.indexOf(GET) < 0) {
            builder.append(GET);
        } else {
            builder.append("&");
        }
        requestParams.forEach((k, v) -> builder.append(k).append(encodeValue(v)).append("&"));
        return builder.substring(0, builder.length() - 1);
    }

    /**
     * 组装post 请求参数
     * @param request
     * @param tags
     * @return
     */
    private Request buildPostRequest(HttpRequest request, String... tags) {
        Headers.Builder header = buildHeader(request);
        String tag = convertTag(tags);
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (request.getRequestParams() != null) {
            request.getRequestParams().forEach((k, v) -> formBuilder.add(k, v == null ? EMPTY : v.toString()));
        }
        return new Request.Builder().headers(header.build()).post(formBuilder.build()).url(request.getUrl()).tag(tag).build();
    }
    /**
     * 构建get请求参数
     * @param request
     * @param tags
     * @return
     */
    private Request buildGetRequest(HttpRequest request, String... tags) {
        Headers.Builder header = buildHeader(request);
        String tag = convertTag(tags);
        String url = buildGetUrl(request);
        return new Request.Builder().headers(header.build()).url(url).tag(tag).build();
    }

    /**
     * 构建post body方式请求参数
     *
     * @param request
     * @param contentType
     * @param tags
     * @return
     */
    private Request buildPostBodyRequest(HttpRequest request, String contentType, String... tags) {
        Headers.Builder header = buildHeader(request);
        String tag = convertTag(tags);
        RequestBody requestBody = FormBody.create(MediaType.parse(contentType), request.getRequestBody());
        return new Request.Builder().headers(header.build()).post(requestBody).url(request.getUrl()).tag(tag).build();
    }

    /**
     * 编码
     *
     * @param value
     * @return
     */
    private String encodeValue(Object value) {
        if (value == null) {
            return EMPTY;
        }
        try {
            return URLEncoder.encode(value.toString(), UTF_8);
        } catch (UnsupportedEncodingException e) {
            return EMPTY;
        }
    }

    /**
     * 解析下载内容
     *
     * @param request
     * @param okHttpRequest
     */
    private void parseDownResponse(Request request, HttpRequest okHttpRequest) {
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LOGGER.error("url:[{}]", okHttpRequest.getUrl(), e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                Sink sink;
                BufferedSink bufferedSink = null;
                try {
                    File dest = new File(okHttpRequest.getPath());
                    sink = Okio.sink(dest);
                    bufferedSink = Okio.buffer(sink);
                    bufferedSink.writeAll(response.body().source());
                    bufferedSink.close();
                } catch (Exception e) {
                    LOGGER.error("parseDownResponse exception url:[{}]", okHttpRequest.getUrl(), e);
                } finally {
                    if (bufferedSink != null) {
                        try {
                            bufferedSink.close();
                        } catch (IOException e) {
                            LOGGER.error("parseDownResponse exception url:[{}]", okHttpRequest.getUrl(), e);
                        }
                    }
                    if (response != null && response.body() != null) {
                        response.close();
                    }
                }
            }
        });
    }
}
