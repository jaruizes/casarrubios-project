package com.jaruiz.casarrubios.recruiters.services.applications.util.openai;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;

import static org.mockito.Mockito.*;

@TestConfiguration
public class TestOpenAiConfig {

    @Bean
    public OpenAiChatModel mockOpenAiChatModel() {
        OpenAiChatModel mockModel = mock(OpenAiChatModel.class);

        when(mockModel.call(anyString())).thenReturn(getFakeResponse());

        return mockModel;
    }

    private String getFakeResponse() {
        return """
            {
                "summary": "Senior Architect with 20+ years of experience.",
                "strengths": ["Expert in cloud", "Strong leadership"],
                "concerns": ["English level at B2"],
                "hardSkills": [{"skill": "Java", "level": 5}],
                "softSkills": [{"skill": "Leadership", "level": 5}],
                "keyResponsibilities": ["Architect complex solutions"],
                "interviewQuestions": ["Explain how you designed an event-driven system"],
                "totalYearsExperience": 20,
                "averagePermanency": 3.5,
                "tags": ["Cloud", "Kafka"]
            }
            """;
    }
}
