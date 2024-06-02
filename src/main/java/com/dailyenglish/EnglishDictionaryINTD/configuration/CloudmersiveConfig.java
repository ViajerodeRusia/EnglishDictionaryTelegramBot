package com.dailyenglish.EnglishDictionaryINTD.configuration;

import com.cloudmersive.client.invoker.ApiClient;
import com.cloudmersive.client.invoker.auth.ApiKeyAuth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudmersiveConfig {
    @Bean
    public ApiClient apiClient() {
        ApiClient defaultClient = com.cloudmersive.client.invoker.Configuration.getDefaultApiClient();
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("Apikey");
        apiKeyAuth.setApiKey("fec6de23-4534-46e6-84f7-bdd4872a14c3");
        return defaultClient;
    }
}
