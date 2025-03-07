package com.jaruiz.casarrubios.recruiters.services.applications.business.ports;

import java.util.List;

public interface EmbeddingServicePort {
    float[] getEmbedding(String text);
}
