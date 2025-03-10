package com.jaruiz.casarrubios.recruiters.services.applications.infrastructure;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Getter @Configuration
@EnableKafka
public class Config {
    public static final String APPLICATIONS_DQL_TOPIC = "applications-to-dql";
    public static final String APPLICATIONS_RECEIVED_TOPIC = "applications-received";
    public static final String APPLICATIONS_ANALYSED_TOPIC = "applications-analysed";

    @Value("${cv.bucket.name}")
    private String bucketName;

}
