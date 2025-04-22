package com.jaruiz.casarrubios.recruiters.services.globalposition.api.rest;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.jaruiz.casarrubios.recruiters.services.globalposition.api.rest.dto.insightsDTO;
import com.jaruiz.casarrubios.recruiters.services.globalposition.business.insightsTopology;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.jboss.logging.Logger;
import static com.jaruiz.casarrubios.recruiters.services.globalposition.business.insightsTopology.*;
import static org.apache.kafka.streams.StoreQueryParameters.fromNameAndType;

@Path("/insights")
@ApplicationScoped
public class insightsRestAPI {

    private static final Logger logger = Logger.getLogger(insightsTopology.class);
    public static final String COUNT = "count";

    private final KafkaStreams streams;

    public insightsRestAPI(KafkaStreams streams) {
        this.streams = streams;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGlobalPosition() {
        logger.info("Getting global position");
        final insightsDTO insightsDTO = new insightsDTO();

        if (streams != null && streams.state() == KafkaStreams.State.RUNNING) {

            ReadOnlyKeyValueStore<String, Long> positions = streams.store(fromNameAndType(POSITIONS_STORE, QueryableStoreTypes.keyValueStore()));
            ReadOnlyKeyValueStore<String, Long> applications = streams.store(fromNameAndType(APPLICATIONS_STORE, QueryableStoreTypes.keyValueStore()));
            ReadOnlyKeyValueStore<String, Double> scoring = streams.store(fromNameAndType(SCORING_STORE, QueryableStoreTypes.keyValueStore()));

            long posCount = positions.get(COUNT) != null ? positions.get(COUNT) : 0L;
            long appCount = applications.get(COUNT) != null ? applications.get(COUNT) : 0L;
            double scoreAvg = scoring.get("average") != null ? scoring.get("average") : 0.0;
            double avgAppPerPos = posCount == 0 ? 0 : (double) appCount / posCount;

            BigDecimal roundedScoreAvg = BigDecimal.valueOf(scoreAvg).setScale(2, RoundingMode.HALF_UP);
            BigDecimal roundedAvgAppPerPos = BigDecimal.valueOf(avgAppPerPos).setScale(2, RoundingMode.HALF_UP);

            insightsDTO.setTotalPositions(posCount);
            insightsDTO.setAverageApplications(roundedAvgAppPerPos.doubleValue());
            insightsDTO.setAverageScore(roundedScoreAvg.doubleValue());
        }

        return Response.ok().entity(insightsDTO).build();
    }
}
