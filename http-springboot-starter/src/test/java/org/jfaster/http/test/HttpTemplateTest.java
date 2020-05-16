package org.jfaster.http.test;

import org.ifaster.http.common.config.HttpRequest;
import org.ifaster.http.common.config.HttpResponse;
import org.ifaster.http.common.remote.HttpTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = HttpTestApplication.class)
@ContextConfiguration
public class HttpTemplateTest {

    @Autowired
    @Qualifier("httpClient")
    private HttpTemplate httpTemplate;

    /**
     * get测试
     */
    @Test
    public void getTest() {
        HttpResponse response = httpTemplate.get("https://www.baidu.com/");
        System.out.println(response);
    }

    /**
     * get测试
     */
    @Test
    public void postTest() {
        HttpRequest request = HttpRequest.builder().url("https://www.baidu.com/").build();
        HttpResponse response = httpTemplate.post(request);
        System.out.println(response);
    }
}

