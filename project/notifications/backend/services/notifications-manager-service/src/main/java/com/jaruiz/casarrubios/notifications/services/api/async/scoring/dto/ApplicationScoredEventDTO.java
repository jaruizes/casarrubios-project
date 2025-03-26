package com.jaruiz.casarrubios.notifications.services.api.async.scoring.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApplicationScoredEventDTO {
    private String applicationId;
    private int positionId;
    private ResumeAnalysisDTO analysis;
    private ScoringDTO scoring;

    @Data
    @NoArgsConstructor
    public static class ResumeAnalysisDTO {

        private String summary;
        private List<String> strengths;
        private List<String> concerns;
        private List<SkillDTO> hardSkills;
        private List<SkillDTO> softSkills;
        private List<String> keyResponsibilities;
        private List<String> interviewQuestions;
        private int totalYearsExperience;
        private BigDecimal averagePermanency;
        private List<String> tags;
    }

    @Data
    @NoArgsConstructor
    public static class ScoringDTO {
        private BigDecimal score;
        private BigDecimal descScore;
        private BigDecimal requirementScore;
        private BigDecimal tasksScore;
        private BigDecimal timeSpent;
        private String explanation;
    }

    @Data
    @NoArgsConstructor
    public static class SkillDTO {
        private String skill;
        private int level;
    }
}
