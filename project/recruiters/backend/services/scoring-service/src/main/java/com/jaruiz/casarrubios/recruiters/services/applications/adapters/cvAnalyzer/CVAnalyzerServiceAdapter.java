package com.jaruiz.casarrubios.recruiters.services.applications.adapters.cvAnalyzer;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.CVAnalysis;
import com.jaruiz.casarrubios.recruiters.services.applications.business.ports.CVAnalyzerServicePort;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

@Service
public class CVAnalyzerServiceAdapter implements CVAnalyzerServicePort {

    private final OpenAiChatModel chatModel;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CVAnalyzerServiceAdapter(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public CVAnalysis analyze(String resumeText) {
        String prompt = """
            You are an AI specialized in analyzing resumes. Extract the following structured information:
            
            - **Summary** (max 400 words).
            - **Key Points** (list of max 5 items).
            - **Weak Points** (list of max 5 items).
            - **Potential Interview Questions** (list of max 5 questions).
            - **Total Years of Experience**.
            - **Tags** (list of max 6 relevant tags).

            Format the output as a JSON object with the following structure:
            {
                "summary": "A summary of the resume",
                "keyPoints": ["key point 1", "key point 2", "key point 3"],
                "weakPoints": ["weak point 1", "weak point 2", "weak point 3"],
                "interviewQuestions": ["question 1", "question 2", "question 3"],
                "totalYearsExperience": 5,
                "tags": ["tag 1", "tag 2", "tag 3"]
            }

            Resume Text:
            %s
        """.formatted(resumeText);

        String response = cleanJsonResponse(chatModel.call(prompt));
        return parseJson(response);
    }

    private String cleanJsonResponse(String jsonResponse) {
        return jsonResponse
            .replace("```json", "")
            .replace("```", "")
            .trim();
    }

    private CVAnalysis parseJson(String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, CVAnalysis.class);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing LLM response", e);
        }
    }

}
