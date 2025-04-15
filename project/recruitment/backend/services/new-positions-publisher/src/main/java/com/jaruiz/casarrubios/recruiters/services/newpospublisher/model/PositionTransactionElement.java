package com.jaruiz.casarrubios.recruiters.services.newpospublisher.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PositionTransactionElement {
    public static final String TYPE_POSITION = "position";
    public static final String TYPE_REQUIREMENT = "requirement";
    public static final String TYPE_BENEFIT = "benefit";
    public static final String TYPE_TASK = "task";
    final String type;
    final Position position;
    final PositionRequirement requirement;
    final PositionBenefit benefit;
    final PositionTask task;

    public PositionTransactionElement() {
        this.type = null;
        this.requirement = null;
        this.benefit = null;
        this.task = null;
        this.position = null;
    }

    public PositionTransactionElement(Position position) {
        this.type = TYPE_POSITION;
        this.requirement = null;
        this.benefit = null;
        this.task = null;
        this.position = position;
    }

    public PositionTransactionElement(PositionRequirement requirement) {
        this.type = TYPE_REQUIREMENT;
        this.requirement = requirement;
        this.benefit = null;
        this.task = null;
        this.position = null;
    }

    public PositionTransactionElement(PositionBenefit benefit) {
        this.type = TYPE_BENEFIT;
        this.benefit = benefit;
        this.requirement = null;
        this.task = null;
        this.position = null;
    }

    public PositionTransactionElement(PositionTask task) {
        this.type = TYPE_TASK;
        this.task = task;
        this.requirement = null;
        this.benefit = null;
        this.position = null;
    }


}
