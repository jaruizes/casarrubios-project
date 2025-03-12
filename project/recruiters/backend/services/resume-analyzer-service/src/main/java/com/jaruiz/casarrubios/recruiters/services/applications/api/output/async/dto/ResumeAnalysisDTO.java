package com.jaruiz.casarrubios.recruiters.services.applications.api.output.async.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeAnalysisDTO {

    private String summary;
    private List<String> strengths;
    private List<String> concerns;
    private List<SkillDTO> hardSkills;
    private List<SkillDTO> softSkills;
    private List<String> keyResponsibilities;
    private List<String> interviewQuestions;
    private int totalYearsExperience;
    private double averagePermanency;
    private List<String> tags;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SkillDTO {
        private String skill;
        private int level;
    }
}
