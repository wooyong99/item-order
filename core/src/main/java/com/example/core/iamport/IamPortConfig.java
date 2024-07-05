package com.example.core.iamport;

import com.siot.IamportRestClient.IamportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IamPortConfig {

    @Value("${imp.api.key}")
    private String key;

    @Value("${imp.api.secretkey}")
    private String secretkey;

    @Bean
    public IamportClient iamportClient() {
        return new IamportClient(key, secretkey);
    }
}
