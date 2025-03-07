package com.jaruiz.casarrubios.recruiters.services.applications.business;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.jaruiz.casarrubios.recruiters.services.applications.business.exceptions.CVNotFoundException;
import com.jaruiz.casarrubios.recruiters.services.applications.business.helper.PositionTextFormatter;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.*;
import com.jaruiz.casarrubios.recruiters.services.applications.business.ports.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ScoringService {
    private static final Logger logger = LoggerFactory.getLogger(ScoringService.class);

    private final CVServicePort cvService;
    private final TextExtractorPort textExtractor;
    private final EmbeddingServicePort embeddingService;
    private final PositionsServicePort positionsService;
    private final CVAnalyzerServicePort cvAnalyzerService;

    public ScoringService(CVServicePort cvService, TextExtractorPort textExtractor, EmbeddingServicePort embeddingService, PositionsServicePort positionsService, CVAnalyzerServicePort cvAnalyzerService) {
        this.cvService = cvService;
        this.textExtractor = textExtractor;
        this.embeddingService = embeddingService;
        this.positionsService = positionsService;
        this.cvAnalyzerService = cvAnalyzerService;
    }

    public Score evaluateApplication(Application application) {
        Score score = null;
        try {

            final String cvText = getTextFromCv(application.getId());
            CVAnalysis cvAnalysis = cvAnalyzerService.analyze(cvText);
            logger.info("Analyzed CV with id {} with analysis {}", application.getId(), cvAnalysis);

            score = scoreApplication(cvText, cvAnalysis, application.getPositionId());
            logger.info("Scored application with id {} with score {}", application.getId(), score);
        } catch (Exception e) {
            logger.error("Error evaluating application with id {}", application.getId());
            logger.error(e.getMessage());
        }

        return score;
    }

    private Score scoreApplication(String cvText, CVAnalysis cvAnalysis, long positionId) {
        final Position position = positionsService.getPositionById(positionId);
        final String positionText = PositionTextFormatter.convertPositionToText(position);
        final List<String> positionTags = List.of(position.getTags().split(","));
        final List<String> mandatoryRequirements = position.getRequirements().stream()
            .filter(Requirement::getMandatory)
            .map(Requirement::getKey)
            .toList();

        double embeddingScore = calculateMatchScoreFromEmbeddings(cvText, positionText);
        double requirementsMatch = calculateRequirementsMatch(cvAnalysis.getKeyPoints(), mandatoryRequirements);
        double tagSimilarity = calculateTagSimilarity(cvAnalysis.getTags(), positionTags);
        double score = calculateFinalScore(embeddingScore, requirementsMatch, tagSimilarity);

        logger.info("Scored application with embedding score {}, requirements match {}, tag similarity {}, final score {}", embeddingScore, requirementsMatch, tagSimilarity, score);

        return new Score(embeddingScore, requirementsMatch, tagSimilarity, score);

    }

    private double calculateFinalScore(double embeddingScore, double requirementsMatch, double tagSimilarity) {
        return (0.3 * embeddingScore) + (0.4 * requirementsMatch) + (0.3 * tagSimilarity);
    }

    private String getTextFromCv(UUID applicationId) throws CVNotFoundException {
        byte[] cv = getCv(applicationId);
        return textExtractor.extractTextFromCV(cv);
    }

    private double calculateMatchScoreFromEmbeddings(String cvText, String positionText) {
        float[] resumeEmbedding = embeddingService.getEmbedding(cvText);
        float[] jobEmbedding = embeddingService.getEmbedding(positionText);

        return cosineSimilarity(resumeEmbedding, jobEmbedding) * 100;
    }

    private double calculateRequirementsMatch(List<String> cvSkills, List<String> positionMandatoryRequirements) {
        final String cvSkillsString = String.join(" ", cvSkills);
        final String positionSkillsString = String.join(" ", positionMandatoryRequirements);

        float[] cvSkillsEmbeddings = embeddingService.getEmbedding(cvSkillsString);
        float[] positionSkillsEmbedding = embeddingService.getEmbedding(positionSkillsString);

        return cosineSimilarity(cvSkillsEmbeddings, positionSkillsEmbedding) * 100;
    }

    private double calculateTagSimilarity(List<String> cvTags, List<String> positionTags) {
        final String cvTagsString = String.join(" ", cvTags);
        final String positionTagsString = String.join(" ", positionTags);

        float[] cvTagsEmbeddings = embeddingService.getEmbedding(cvTagsString);
        float[] positionTagsEmbeddings = embeddingService.getEmbedding(positionTagsString);

        return cosineSimilarity(cvTagsEmbeddings, positionTagsEmbeddings) * 100;
    }

    private double cosineSimilarity(float[] cvVectors, float[] positionVectors) {
        double dotProduct = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < cvVectors.length; i++) {
            dotProduct += cvVectors[i] * positionVectors[i];
            normA += Math.pow(cvVectors[i], 2);
            normB += Math.pow(positionVectors[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private byte[] getCv(UUID applicationId) throws CVNotFoundException {
        return cvService.getCV(applicationId.toString());
    }

}
