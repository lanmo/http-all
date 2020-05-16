package org.ifaster.http.springboot.configuration;

import lombok.extern.slf4j.Slf4j;
import org.ifaster.http.common.config.HttpClientConfig;
import org.ifaster.http.common.enums.ClientType;
import org.ifaster.http.common.remote.HttpTemplate;
import org.ifaster.http.common.remote.OkHttpTemplate;
import org.ifaster.http.springboot.config.ConfigFactory;
import org.ifaster.http.springboot.config.HttpClientProperty;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.ifaster.http.common.constant.HttpConstant.CONFIG_PREFIX;

/**
 * @author yangnan
 */
@Slf4j
@Configuration
@ConditionalOnClass(value = {HttpClientConfig.class})
public class HttpAutoCreator implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;
        HttpClientProperty config = ConfigFactory.getConfig(dlbf, HttpClientProperty.class, CONFIG_PREFIX);
        if (config == null || CollectionUtils.isEmpty(config.getClientConfigs())) {
            log.warn("not config [{}]", CONFIG_PREFIX);
            return;
        }
        config.getClientConfigs().forEach(r -> registerBean(r, dlbf));
    }

    /**
     * registerBean
     *
     * @param clientConfig http-client config
     * @param dlbf
     */
    private void registerBean(HttpClientConfig clientConfig, DefaultListableBeanFactory dlbf) {
        BeanDefinitionBuilder factory = null;
        if (clientConfig.getClientType() == ClientType.OK_HTTP) {
            factory = BeanDefinitionBuilder.genericBeanDefinition(OkHttpTemplate.class);
            factory.addConstructorArgValue(clientConfig);
        } else if (clientConfig.getClientType() == ClientType.HTTP_CLIENT) {
            throw new IllegalStateException("not support ClientType.HTTP_CLIENT");
        }
        if (factory == null) {
            throw new IllegalStateException("not config clientType");
        }
        factory.setInitMethodName("init");
        dlbf.registerBeanDefinition(clientConfig.getClientName(), factory.getBeanDefinition());
    }
}
