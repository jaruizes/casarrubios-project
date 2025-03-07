package com.jaruiz.casarrubios.recruiters.services.applications.business.model;

import java.util.List;

public class CVAnalysis {
    private String summary;
    private List<String> keyPoints;
    private List<String> weakPoints;
    private List<String> interviewQuestions;
    private int totalYearsExperience;
    private List<String> tags;

    // Getters y Setters
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<String> getKeyPoints() { return keyPoints; }
    public void setKeyPoints(List<String> keyPoints) { this.keyPoints = keyPoints; }

    public List<String> getWeakPoints() { return weakPoints; }
    public void setWeakPoints(List<String> weakPoints) { this.weakPoints = weakPoints; }

    public List<String> getInterviewQuestions() { return interviewQuestions; }
    public void setInterviewQuestions(List<String> interviewQuestions) { this.interviewQuestions = interviewQuestions; }

    public int getTotalYearsExperience() { return totalYearsExperience; }
    public void setTotalYearsExperience(int totalYearsExperience) { this.totalYearsExperience = totalYearsExperience; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
