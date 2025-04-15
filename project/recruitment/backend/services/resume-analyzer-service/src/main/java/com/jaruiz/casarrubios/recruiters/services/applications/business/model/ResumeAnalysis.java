package com.jaruiz.casarrubios.recruiters.services.applications.business.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeAnalysis {
    private String summary;
    private List<String> strengths;
    private List<String> concerns;
    private List<String> keyResponsibilities;
    private List<Skill> hardSkills;
    private List<Skill> softSkills;
    private List<String> interviewQuestions;
    private int totalYearsExperience;
    private float averagePermanency;
    private List<String> tags;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Skill {
        private String skill;
        private int level;
    }
}
