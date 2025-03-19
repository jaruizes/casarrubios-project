package com.jaruiz.casarrubios.recruiters.services.newpospublisher.utils.kafka;

import com.jaruiz.casarrubios.recruiters.services.newpospublisher.model.Position;
import com.jaruiz.casarrubios.recruiters.services.newpospublisher.model.PositionBenefit;
import com.jaruiz.casarrubios.recruiters.services.newpospublisher.model.PositionRequirement;
import com.jaruiz.casarrubios.recruiters.services.newpospublisher.model.PositionTask;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

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

        positionCDCEmitter.send(position);
    }

    public void publishRequirement(long positionId, long requirementId) {
        PositionRequirement requirement = new PositionRequirement();
        requirement.setPositionId(positionId);
        requirement.setDescription("Description");
        requirement.setKey("Key");
        requirement.setValue("Value");
        requirement.setId(requirementId);
        requirement.setMandatory(true);

        positionRequirementsCDCEmitter.send(requirement);
    }

    public void publishTask(long positionId, long taskId) {
        PositionTask task = new PositionTask();
        task.setPositionId(positionId);
        task.setDescription("Description");
        task.setId(taskId);

        positionTasksCDCEmitter.send(task);
    }

    public void publishBenefit(long positionId, long benefitId) {
        PositionBenefit benefit = new PositionBenefit();
        benefit.setPositionId(positionId);
        benefit.setDescription("Description");
        benefit.setId(benefitId);

        positionBenefitCDCEmitter.send(benefit);
    }

}
