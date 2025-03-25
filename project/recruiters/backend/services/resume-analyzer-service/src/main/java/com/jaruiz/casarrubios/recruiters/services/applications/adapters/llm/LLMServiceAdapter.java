package com.jaruiz.casarrubios.recruiters.services.applications.adapters.llm;

import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaruiz.casarrubios.recruiters.services.applications.business.exceptions.AnalysingException;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.ResumeAnalysis;
import com.jaruiz.casarrubios.recruiters.services.applications.business.ports.LLMServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

@Service
public class LLMServiceAdapter implements LLMServicePort {
    private static final Logger logger = LoggerFactory.getLogger(LLMServiceAdapter.class);
    private final OpenAiChatModel chatModel;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LLMServiceAdapter(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public ResumeAnalysis analyze(UUID applicationId, String resumeText) throws AnalysingException {
        String response = chatModel.call(buildPrompt(resumeText));
        if (response == null || response.isBlank()) {
            logger.error("[Application Id: {}] Not response from LLL", applicationId);
            throw new AnalysingException("[Application Id: " + applicationId.toString() + "] Not response from LLL", AnalysingException.CODE_LLM_RESPONSE_NULL_OR_EMPTY);
        }

        logger.info("[Application Id: {}] LLM response received", applicationId);
        return parseJson(cleanJsonResponse(response));
    }

    private String cleanJsonResponse(String jsonResponse) {
        return jsonResponse
            .replace("```json", "")
            .replace("```", "")
            .trim();
    }

    private ResumeAnalysis parseJson(String jsonResponse) throws AnalysingException {
        try {
            return objectMapper.readValue(jsonResponse, ResumeAnalysis.class);
        } catch (JsonProcessingException e) {
            logger.error(jsonResponse);
            logger.error(e.getMessage());
            throw new AnalysingException("Error parsing LLM response", AnalysingException.CODE_LLM_RESPONSE_PARSING_ERROR);
        }
    }

    // TODO: extact to a file
    private String buildPrompt(String resumeText) {
        return """
        You are a technology recruiter. Analyze the following resume and extract structured information in JSON format.
        Response must be written in Spanish
        Use the following rules:

        - **Summary (max 400 words)**: A concise description of the candidate's expertise, experience, and unique value.
        - **Strengths & Concerns**:  
          - **Strengths**: Key differentiators of the candidate’s profile.  
          - **Concerns**: Areas that might raise questions for recruiters (e.g., job transitions, skills gaps, business alignment).
        - **Hard Skills & Proficiency Levels (Scale 1-3)**: Evaluate technical skills and assign a level:  
          - **1 = Basic** (Familiarity but limited hands-on experience).  
          - **2 = Intermediate** (Able to use in projects but not deeply specialized).  
          - **3 = Advanced** (Strong practical knowledge. Capable of teaching, leading, and innovating).
        - **Soft Skills & Proficiency Levels (Scale 1-3)**: Identify key soft skills and assign a level using the same scale.  
        - **Key Responsibilities (last 5 years, max 8 items)**: List major tasks and achievements.
        - **Potential Interview Questions**: according to the resume, propose max 10 questions about career, strengths, concerns, skills, responsibilities and experience. A flat list of questions is required (you must not group them by category).
        - **Total Years of Experience**.  
        - **Average Job Permanency (in years)**.  
        - **Relevant Tags**: Keywords that best describe the candidate.
        
        **Resume Data:**
        ```text
        %s
        ```
        **Output JSON Format:**
        ```json
        {
            "summary": "A summary of the resume",
            "strengths": ["Key strength 1", "Key strength 2"],
            "concerns": ["Concern 1", "Concern 2"],
            "hardSkills": [{"skill": "Java", "level": 5}, {"skill": "AWS", "level": 4}],
            "softSkills": [{"skill": "Leadership", "level": 5}, {"skill": "Communication", "level": 4}],
            "keyResponsibilities": ["Task 1", "Task 2"],
            "interviewQuestions": ["Question 1", "Question 2"],
            "totalYearsExperience": 10,
            "averagePermanency": 3.5,
            "tags": ["Tag 1", "Tag 2"]
        }
        ```
        """.formatted(resumeText);
    }

}
