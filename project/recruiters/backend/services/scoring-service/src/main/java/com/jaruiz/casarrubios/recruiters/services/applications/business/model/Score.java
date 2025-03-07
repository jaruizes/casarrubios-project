package com.jaruiz.casarrubios.recruiters.services.applications.business.model;

public class Score {
    private double score;
    private double embeddingScore;
    private double requirementsMatch;
    private double tagSimilarity;

    public Score(double embeddingScore, double requirementsMatch, double tagSimilarity, double score) {
        this.embeddingScore = embeddingScore;
        this.requirementsMatch = requirementsMatch;
        this.tagSimilarity = tagSimilarity;
        this.score = score;
    }

    public double getScore() {
        return score;
    }

    public double getEmbeddingScore() {
        return embeddingScore;
    }

    public double getRequirementsMatch() {
        return requirementsMatch;
    }

    public double getTagSimilarity() {
        return tagSimilarity;
    }

    @Override public String toString() {
        return "Score{" + "score=" + score + ", embeddingScore=" + embeddingScore + ", requirementsMatch=" + requirementsMatch + ", tagSimilarity=" + tagSimilarity + '}';
    }
}
