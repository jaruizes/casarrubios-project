package com.jaruiz.casarrubios.recruiters.services.applications.infrastructure;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Getter @Configuration
@EnableKafka
public class Config {
    @Value("${cv.bucket.name}")
    private String bucketName;

    @Value("${applications-received.topic:recruitment.applications-received}")
    private String applicationsReceivedTopic;

    @Value("${applications-received.topic:recruitment.applications-analyzed}")
    private String applicationsAnalyzedTopic;

    @Value("${applications-dlq.topic:recruitment.applications-dlq}")
    private String dlqTopic;

}
