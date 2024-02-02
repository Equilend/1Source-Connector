package com.intellecteu.onesource.integration.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.client.spire.InstructionSpireApiClient;
import com.intellecteu.onesource.integration.services.client.spire.PositionSpireApiClient;
import com.intellecteu.onesource.integration.services.client.spire.TradeSpireApiClient;
import com.intellecteu.onesource.integration.services.client.spire.invoker.ApiClient;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

    @Bean("spireApiClient")
    public ApiClient spireApiClient(RestTemplate restTemplate,
        @Value("${spire.baseEndpoint}") String spireBasePath) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(spireBasePath);
        return apiClient;
    }

    @Bean("lenderApiClient") // todo rework to use only one client
    public ApiClient lenderApiClient(RestTemplate restTemplate,
        @Value("${spire.lenderEndpoint}") String spireBasePath) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(spireBasePath);
        return apiClient;
    }

    @Bean("borrowerApiClient") // todo rework to use only one client
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

    @Bean
    public TradeSpireApiClient lenderTradeSpireApiClient(ApiClient lenderApiClient,
        @Value("${spire.username}") String clientId) {
        return new TradeSpireApiClient(lenderApiClient, clientId);
    }

    @Bean
    public TradeSpireApiClient borrowerTradeSpireApiClient(ApiClient borrowerApiClient,
        @Value("${spire.username}") String clientId) {
        return new TradeSpireApiClient(borrowerApiClient, clientId);
    }

    @Bean("lenderBackOfficeService") // todo rework to use only one backoffice
    public BackOfficeService lenderBackOfficeService(PositionSpireApiClient lenderPositionSpireApiClient,
        TradeSpireApiClient lenderTradeSpireApiClient, InstructionSpireApiClient instructionClient,
        SpireMapper spireMapper, BackOfficeMapper backOfficeMapper, CloudEventRecordService cloudEventRecordService) {
        return new BackOfficeService(lenderPositionSpireApiClient, lenderTradeSpireApiClient, instructionClient,
            spireMapper, backOfficeMapper, cloudEventRecordService);
    }

    @Bean("borrowerBackOfficeService") // todo rework to use only one backoffice
    public BackOfficeService borrowerBackOfficeService(PositionSpireApiClient borrowerPositionSpireApiClient,
        TradeSpireApiClient borrowerTradeSpireApiClient, InstructionSpireApiClient instructionClient,
        SpireMapper spireMapper, BackOfficeMapper backOfficeMapper, CloudEventRecordService cloudEventRecordService) {
        return new BackOfficeService(borrowerPositionSpireApiClient, borrowerTradeSpireApiClient, instructionClient,
            spireMapper, backOfficeMapper, cloudEventRecordService);
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
