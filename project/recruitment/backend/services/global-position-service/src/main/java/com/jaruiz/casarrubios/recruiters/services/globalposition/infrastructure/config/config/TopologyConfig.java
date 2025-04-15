package com.jaruiz.casarrubios.recruiters.services.globalposition.infrastructure.config.config;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Getter
public class TopologyConfig {

    @ConfigProperty(name ="new-positions.topic", defaultValue = "recruiters.new-positions-published")
    String newPositionsTopic;

    @ConfigProperty(name ="scoring.topic", defaultValue = "recruitment.applications-scored")
    String scoringTopic;

    @ConfigProperty(name ="new-applications.topic", defaultValue = "recruitment.applications-received")
    String newApplicationsTopic;


}
