package com.jaruiz.casarrubios.recruiters.services.applications.util.openai;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class SetupOpenAI {

    @Bean
    public OpenAiChatModel mockOpenAiChatModel() {
        return mock(OpenAiChatModel.class);
    }

    public static String getFakeResponse() {
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

    public static String getWrongFakeResponse() {
        return """
            {
                "summary": "Senior Architect with 20+ years of experience.",
                "strengths": ["Expert in cloud", "Strong leadership"],
                "consddscerns": ["English level at B2"],
                "hardsddsSkills": [{"skill": "Java", "level": 5}],
                "softdsdsSkills": [{"skill": "Leadership", "level": 5}],
                "keyRsdaesponsibilities": ["Architect complex solutions"],
                "interviewQuestions": ["Explain how you designed an event-driven system"],
                "totadsadaslYearsExperience": 20,
                "averagePermanency": 3.5,
                "tags": ["Cloud", "Kafka"]
            }
            """;
    }
}
