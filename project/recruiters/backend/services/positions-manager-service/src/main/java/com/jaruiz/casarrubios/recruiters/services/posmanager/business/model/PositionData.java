package com.jaruiz.casarrubios.recruiters.services.posmanager.business.model;

import java.util.ArrayList;
import java.util.List;

public class PositionData {
    private final String title;
    private final String description;
    private final List<Requirement> requirements;
    private final List<Benefit> benefits;
    private final List<Task> tasks;

    public PositionData(String title, String description) {
        this.title = title;
        this.description = description;
        this.requirements = new ArrayList<>();
        this.benefits = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public List<Benefit> getBenefits() {
        return benefits;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void addRequirements(List<Requirement> requirements) {
        this.requirements.addAll(requirements);
    }

    public void addBenefits(List<Benefit> benefits) {
        this.benefits.addAll(benefits);
    }

    public void addTasks(List<Task> tasks) {
        this.tasks.addAll(tasks);
    }

    public boolean isValid() {
        return (getTitle() != null && !getTitle().isEmpty()) &&
            (getDescription() != null && !getDescription().isEmpty()) &&
            areRequirementsValid() &&
            areBenefitsValid() &&
            areTasksValid();
    }

    private boolean areRequirementsValid() {
        final List<Requirement> requirements = getRequirements();
        if (requirements != null && !requirements.isEmpty()) {
            return requirements.stream().allMatch(Requirement::isValid);
        }

        return false;
    }

    private boolean areBenefitsValid() {
        final List<Benefit> benefits = getBenefits();
        if (benefits != null && !benefits.isEmpty()) {
            return benefits.stream().allMatch(Benefit::isValid);
        }

        return false;
    }

    private boolean areTasksValid() {
        final List<Task> tasks = getTasks();
        if (tasks != null && !tasks.isEmpty()) {
            return tasks.stream().allMatch(Task::isValid);
        }

        return false;
    }
}
