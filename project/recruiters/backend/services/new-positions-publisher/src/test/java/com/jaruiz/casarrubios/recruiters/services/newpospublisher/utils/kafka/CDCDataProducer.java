package com.jaruiz.casarrubios.recruiters.services.newpospublisher.utils.kafka;

import com.jaruiz.casarrubios.recruiters.services.newpospublisher.model.*;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

@ApplicationScoped
public class CDCDataProducer {

    @Inject
    @Channel("cdc-recruiters-positions-out") Emitter<Position> positionCDCEmitter;

    @Inject
    @Channel("cdc-recruiters-positions-requirements-out") Emitter<PositionRequirement> positionRequirementsCDCEmitter;

    @Inject
    @Channel("cdc-recruiters-positions-tasks-out") Emitter<PositionTask> positionTasksCDCEmitter;

    @Inject
    @Channel("cdc-recruiters-positions-benefits-out") Emitter<PositionBenefit> positionBenefitCDCEmitter;


    public void publishPosition(long id) {
        Position position = new Position();
        position.setId(id);
        position.setTitle("Title");
        position.setDescription("Description");
        position.setStatus("OPEN");
        position.setCreatedAt(System.currentTimeMillis());
        position.setPublishedAt(System.currentTimeMillis());
        position.setTags("Tag1, Tag2");

        Message<Position> message = Message.of(position)
                                           .addMetadata(OutgoingKafkaRecordMetadata.<PositionKey>builder().withKey(buildPositionKey(id)));
        positionCDCEmitter.send(message);
    }

    public void publishRequirement(long positionId, long requirementId) {
        PositionRequirement requirement = new PositionRequirement();
        requirement.setPositionId(positionId);
        requirement.setDescription("Description");
        requirement.setKey("Key");
        requirement.setValue("Value");
        requirement.setId(requirementId);
        requirement.setMandatory(true);

        Message<PositionRequirement> message = Message.of(requirement)
                                           .addMetadata(OutgoingKafkaRecordMetadata.<PositionKey>builder().withKey(buildPositionKey(positionId)));
        positionRequirementsCDCEmitter.send(message);
    }

    public void publishTask(long positionId, long taskId) {
        PositionTask task = new PositionTask();
        task.setPositionId(positionId);
        task.setDescription("Description");
        task.setId(taskId);

        Message<PositionTask> message = Message.of(task)
                                                      .addMetadata(OutgoingKafkaRecordMetadata.<PositionKey>builder().withKey(buildPositionKey(positionId)));
        positionTasksCDCEmitter.send(message);
    }

    public void publishBenefit(long positionId, long benefitId) {
        PositionBenefit benefit = new PositionBenefit();
        benefit.setPositionId(positionId);
        benefit.setDescription("Description");
        benefit.setId(benefitId);

        Message<PositionBenefit> message = Message.of(benefit)
                                               .addMetadata(OutgoingKafkaRecordMetadata.<PositionKey>builder().withKey(buildPositionKey(positionId)));
        positionBenefitCDCEmitter.send(message);
    }

    private PositionKey buildPositionKey(long id) {
        PositionKey key = new PositionKey();
        key.setId(id);
        return key;
    }

}
