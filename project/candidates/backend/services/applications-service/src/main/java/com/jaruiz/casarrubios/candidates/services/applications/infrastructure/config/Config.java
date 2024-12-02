package com.jaruiz.casarrubios.candidates.services.applications.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${cv.bucket.name}")
    private String bucketName;

    public String getBucketName() {
        return bucketName;
    }
}
