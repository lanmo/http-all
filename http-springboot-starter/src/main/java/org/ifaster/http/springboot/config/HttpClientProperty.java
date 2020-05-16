package org.ifaster.http.springboot.config;

import lombok.Data;
import org.ifaster.http.common.config.HttpClientConfig;

import java.util.List;

/**
 * @author yangnan
 */
@Data
public class HttpClientProperty {
    private List<HttpClientConfig> clientConfigs;
}
