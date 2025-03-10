package com.jaruiz.casarrubios.recruiters.services.applications.adapters.llm;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.ResumeAnalysis;
import com.jaruiz.casarrubios.recruiters.services.applications.business.ports.LLMServicePort;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

@Service
public class LLMServiceAdapter implements LLMServicePort {

    private final OpenAiChatModel chatModel;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LLMServiceAdapter(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public ResumeAnalysis analyze(String resumeText) {
        String response = cleanJsonResponse(chatModel.call(buildPrompt(resumeText)));
        return parseJson(response);
    }

    private String cleanJsonResponse(String jsonResponse) {
        return jsonResponse
            .replace("```json", "")
            .replace("```", "")
            .trim();
    }

    private ResumeAnalysis parseJson(String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, ResumeAnalysis.class);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing LLM response", e);
        }
    }

    private String buildPrompt(String resumeText) {
        return """
        You are a technology recruiter. Analyze the following resume and extract structured information in JSON format.
        Use the following rules:

        - **Summary (max 400 words)**: A concise description of the candidate's expertise, experience, and unique value.
        - **Strengths & Concerns**:  
          - **Strengths**: Key differentiators of the candidate’s profile.  
          - **Concerns**: Areas that might raise questions for recruiters (e.g., job transitions, skills gaps, business alignment).
        - **Hard Skills & Proficiency Levels (Scale 1-5)**: Evaluate technical skills and assign a level:  
          - **1 = Basic** (Familiarity but limited hands-on experience).  
          - **2 = Intermediate** (Able to use in projects but not deeply specialized).  
          - **3 = Advanced** (Strong practical knowledge).  
          - **4 = Highly Advanced** (Deep expertise, frequently optimizes solutions).  
          - **5 = Expert** (Industry-level, capable of teaching, leading, and innovating).  
        - **Soft Skills & Proficiency Levels (Scale 1-5)**: Identify key soft skills and assign a level using the same scale.  
        - **Key Responsibilities (last 5 years, max 8 items)**: List major tasks and achievements.
        - **Potential Interview Questions**:  
          - **Technical** (e.g., architecture, cloud, microservices).  
          - **Behavioral** (e.g., leadership, teamwork).  
          - **Career-based** (e.g., job changes, professional goals).  
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
