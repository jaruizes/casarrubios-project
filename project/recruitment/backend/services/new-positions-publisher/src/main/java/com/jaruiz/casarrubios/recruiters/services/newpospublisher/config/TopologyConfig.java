package com.jaruiz.casarrubios.recruiters.services.newpospublisher.config;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Getter
public class TopologyConfig {

    @ConfigProperty(name ="cdc.recruiters.positions.topic", defaultValue = "cdc.recruiters.positions")
    String cdcPositionsTopic;

    @ConfigProperty(name ="cdc.recruiters.positions-requirements.topic", defaultValue = "cdc.recruiters.positions-requirements")
    String cdcPositionsRequirementsTopic;

    @ConfigProperty(name ="cdc.recruiters.positions-benefits.topic", defaultValue = "cdc.recruiters.positions-benefits")
    String cdcPositionsBenefitsTopic;

    @ConfigProperty(name ="cdc.recruiters.positions-task.topic", defaultValue = "cdc.recruiters.positions-tasks")
    String cdcPositionsTaskTopic;

    @ConfigProperty(name ="recruiters.new-positions-published.topic", defaultValue = "recruiters.new-positions-published")
    String newPositionsPublishedTopic;
}
