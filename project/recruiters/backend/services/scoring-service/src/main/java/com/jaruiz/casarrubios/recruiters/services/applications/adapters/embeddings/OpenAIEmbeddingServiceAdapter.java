package com.jaruiz.casarrubios.recruiters.services.applications.adapters.embeddings;

import com.jaruiz.casarrubios.recruiters.services.applications.business.ports.EmbeddingServicePort;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

@Service
public class OpenAIEmbeddingServiceAdapter implements EmbeddingServicePort {
    private final EmbeddingModel embeddingModel;

    public OpenAIEmbeddingServiceAdapter(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Override public float[] getEmbedding(String text) {
        return this.embeddingModel.embed(text);
    }
}
