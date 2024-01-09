package com.intellecteu.onesource.integration.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.client.spire.PositionSpireApiClient;
import com.intellecteu.onesource.integration.services.client.spire.invoker.ApiClient;
import com.intellecteu.onesource.integration.services.record.CloudEventRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@ComponentScan("com.intellecteu.onesource.integration")
@RequiredArgsConstructor
public class AppConfig {

    private final RestTemplateAuthInterceptor interceptor;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        restTemplate.setMessageConverters(getHttpMessageConverters());
        restTemplate.setInterceptors(getHttpRequestInterceptors(restTemplate.getInterceptors()));
        return restTemplate;
    }

    @Bean
    public ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return objectMapper;
    }

    @Bean("lenderApiClient")
    public ApiClient lenderApiClient(RestTemplate restTemplate,
        @Value("${spire.lenderEndpoint}") String spireBasePath) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(spireBasePath);
        return apiClient;
    }

    @Bean("borrowerApiClient")
    public ApiClient borrowerApiClient(RestTemplate restTemplate,
        @Value("${spire.borrowerEndpoint}") String spireBasePath) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(spireBasePath);
        return apiClient;
    }

    @Bean("lenderPositionSpireApiClient")
    public PositionSpireApiClient lenderPositionSpireApiClient(ApiClient lenderApiClient,
        @Value("${spire.username}") String clientId) {
        return new PositionSpireApiClient(lenderApiClient, clientId);
    }

    @Bean("borrowerPositionSpireApiClient")
    public PositionSpireApiClient borrowerPositionSpireApiClient(ApiClient borrowerApiClient,
        @Value("${spire.username}") String clientId) {
        return new PositionSpireApiClient(borrowerApiClient, clientId);
    }

    @Bean("lenderBackOfficeService")
    public BackOfficeService lenderBackOfficeService(PositionSpireApiClient lenderPositionSpireApiClient,
        SpireMapper spireMapper, CloudEventRecordService cloudEventRecordService) {
        return new BackOfficeService(lenderPositionSpireApiClient, spireMapper, cloudEventRecordService);
    }

    @Bean("borrowerBackOfficeService")
    public BackOfficeService borrowerBackOfficeService(PositionSpireApiClient borrowerPositionSpireApiClient,
        SpireMapper spireMapper, CloudEventRecordService cloudEventRecordService) {
        return new BackOfficeService(borrowerPositionSpireApiClient, spireMapper, cloudEventRecordService);
    }

    private List<ClientHttpRequestInterceptor> getHttpRequestInterceptors(
        List<ClientHttpRequestInterceptor> interceptors) {
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(interceptor);
        return interceptors;
    }

    private List<HttpMessageConverter<?>> getHttpMessageConverters() {
        var messageConverters = new ArrayList<HttpMessageConverter<?>>();
        var converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        return messageConverters;
    }
}
