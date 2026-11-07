package com.ecommerce.notification.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

@Configuration
@ConditionalOnProperty(name = "notifications.enabled", havingValue = "true")
public class AwsClientConfig {

    @Bean
    public SqsClient sqsClient(
            @Value("${aws.region}") String region,
            @Value("${aws.endpoint:}") String endpoint
    ) {
        var builder = SqsClient.builder();
        builder.region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create());
        if (endpoint != null && !endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint));
        }
        return builder.build();
    }

    @Bean
    public SnsClient snsClient(
            @Value("${aws.region}") String region,
            @Value("${aws.endpoint:}") String endpoint
    ) {
        var builder = SnsClient.builder();
        builder.region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create());
        if (endpoint != null && !endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint));
        }
        return builder.build();
    }

    @Bean
    public SesClient sesClient(
            @Value("${aws.region}") String region,
            @Value("${aws.endpoint:}") String endpoint
    ) {
        var builder = SesClient.builder();
        builder.region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create());
        if (endpoint != null && !endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint));
        }
        return builder.build();
    }
}
