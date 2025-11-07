package com.voidsamuraj.HireLens.config;

import com.voidsamuraj.HireLens.client.remoteok.api.DefaultApi;
import com.voidsamuraj.HireLens.service.ai.AiClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Provides API client beans for the application.
 */
@Configuration
public class ApiClientsConfig {

    @Value("${ai-server.address}")
    private String address;
    /**
     * Configures Logger.
     *
     * @return AiClientService to contact with AI
     */
    @Bean
    public AiClientService aiClientService(){
        return new AiClientService(address);
    }


    /**
     * Configures RestTemplate with redirect support.
     *
     * @return configured RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return  new RestTemplateBuilder()
                .redirects(ClientHttpRequestFactorySettings.Redirects.FOLLOW_WHEN_POSSIBLE)
                .build();
    }

    /**
     * Creates RemoteOK API client instance.
     *
     * @return DefaultApi client
     */
    @Bean
    public DefaultApi defaultApi() {
        return new DefaultApi();
    }

}
