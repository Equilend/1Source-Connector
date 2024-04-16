package com.intellecteu.onesource.integration.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
import com.intellecteu.onesource.integration.services.BackOfficeService;
import com.intellecteu.onesource.integration.services.client.spire.InstructionSpireApiClient;
import com.intellecteu.onesource.integration.services.client.spire.PositionSpireApiClient;
import com.intellecteu.onesource.integration.services.client.spire.TradeSpireApiClient;
import com.intellecteu.onesource.integration.services.client.spire.invoker.ApiClient;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;
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
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionManager;
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
        @Value("${spire.base-endpoint}") String spireBasePath) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(spireBasePath);
        return apiClient;
    }

    @Bean("lenderApiClient")
    @Deprecated(since = "0.0.5-SNAPSHOT")
    public ApiClient lenderApiClient(RestTemplate restTemplate,
        @Value("${spire.base-endpoint}") String spireBasePath) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(spireBasePath);
        return apiClient;
    }

    @Bean("borrowerApiClient")
    @Deprecated(since = "0.0.5-SNAPSHOT")
    public ApiClient borrowerApiClient(RestTemplate restTemplate,
        @Value("${spire.base-endpoint}") String spireBasePath) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(spireBasePath);
        return apiClient;
    }

    @Bean
    public PositionSpireApiClient spirePositionApiClient(ApiClient spireApiClient,
        @Value("${spire.username}") String clientId) {
        return new PositionSpireApiClient(spireApiClient, clientId);
    }

    @Bean("lenderPositionSpireApiClient")
    @Deprecated(since = "0.0.5-SNAPSHOT")
    public PositionSpireApiClient lenderPositionSpireApiClient(ApiClient lenderApiClient,
        @Value("${spire.username}") String clientId) {
        return new PositionSpireApiClient(lenderApiClient, clientId);
    }

    @Bean("borrowerPositionSpireApiClient")
    @Deprecated(since = "0.0.5-SNAPSHOT")
    public PositionSpireApiClient borrowerPositionSpireApiClient(ApiClient borrowerApiClient,
        @Value("${spire.username}") String clientId) {
        return new PositionSpireApiClient(borrowerApiClient, clientId);
    }

    @Bean
    public TradeSpireApiClient tradeSpireApiClient(ApiClient spireApiClient,
        @Value("${spire.username}") String clientId) {
        return new TradeSpireApiClient(spireApiClient, clientId);
    }

    @Bean
    @Deprecated(since = "0.0.5-SNAPSHOT")
    public TradeSpireApiClient lenderTradeSpireApiClient(ApiClient lenderApiClient,
        @Value("${spire.username}") String clientId) {
        return new TradeSpireApiClient(lenderApiClient, clientId);
    }

    @Bean
    @Deprecated(since = "0.0.5-SNAPSHOT")
    public TradeSpireApiClient borrowerTradeSpireApiClient(ApiClient borrowerApiClient,
        @Value("${spire.username}") String clientId) {
        return new TradeSpireApiClient(borrowerApiClient, clientId);
    }

    @Bean
    public BackOfficeService backOfficeService(PositionSpireApiClient spirePositionApiClient,
        TradeSpireApiClient tradeSpireApiClient, InstructionSpireApiClient instructionClient,
        @Value("${spire.user-id}") Integer userId, @Value("${spire.username}") String userName,
         BackOfficeMapper backOfficeMapper, CloudEventRecordService cloudEventRecordService) {
        return new BackOfficeService(spirePositionApiClient, tradeSpireApiClient, instructionClient, userId,
            userName, backOfficeMapper, cloudEventRecordService);
    }

    @Bean("lenderBackOfficeService")
    @Deprecated(since = "0.0.5-SNAPSHOT")
    public BackOfficeService lenderBackOfficeService(PositionSpireApiClient lenderPositionSpireApiClient,
        TradeSpireApiClient lenderTradeSpireApiClient, InstructionSpireApiClient instructionClient,
        @Value("${spire.user-id}") Integer userId, @Value("${spire.username}") String userName,
        BackOfficeMapper backOfficeMapper, CloudEventRecordService cloudEventRecordService) {
        return new BackOfficeService(lenderPositionSpireApiClient, lenderTradeSpireApiClient, instructionClient, userId,
            userName, backOfficeMapper, cloudEventRecordService);
    }

    @Bean("borrowerBackOfficeService")
    @Deprecated(since = "0.0.5-SNAPSHOT")
    public BackOfficeService borrowerBackOfficeService(PositionSpireApiClient borrowerPositionSpireApiClient,
        TradeSpireApiClient borrowerTradeSpireApiClient, InstructionSpireApiClient instructionClient,
        @Value("${spire.user-id}") Integer userId, @Value("${spire.username}") String userName,
        BackOfficeMapper backOfficeMapper, CloudEventRecordService cloudEventRecordService) {
        return new BackOfficeService(borrowerPositionSpireApiClient, borrowerTradeSpireApiClient, instructionClient,
            userId, userName, backOfficeMapper, cloudEventRecordService);
    }

    @Bean
    public TransactionManager transactionManager(DataSource dataSource) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setDataSource(dataSource);
        jpaTransactionManager.setGlobalRollbackOnParticipationFailure(false);
        return jpaTransactionManager;
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
        converter.setObjectMapper(objectMapper());
        messageConverters.add(converter);
        return messageConverters;
    }

}
