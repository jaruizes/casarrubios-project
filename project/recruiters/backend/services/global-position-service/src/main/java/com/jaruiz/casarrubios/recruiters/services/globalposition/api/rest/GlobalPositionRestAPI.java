package com.jaruiz.casarrubios.recruiters.services.globalposition.api.rest;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.jaruiz.casarrubios.recruiters.services.globalposition.api.rest.dto.GlobalPositionDTO;
import com.jaruiz.casarrubios.recruiters.services.globalposition.business.GlobalPositionTopology;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.jboss.logging.Logger;
import static com.jaruiz.casarrubios.recruiters.services.globalposition.business.GlobalPositionTopology.*;
import static org.apache.kafka.streams.StoreQueryParameters.fromNameAndType;

@Path("/global-position")
@ApplicationScoped
public class GlobalPositionRestAPI {

    private static final Logger logger = Logger.getLogger(GlobalPositionTopology.class);

    @Inject
    KafkaStreams streams;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGlobalPosition() {
        logger.info("Getting global position");
        final GlobalPositionDTO globalPositionDTO = new GlobalPositionDTO();

        if (streams != null && streams.state() == KafkaStreams.State.RUNNING) {

            ReadOnlyKeyValueStore<String, Long> positions = streams.store(fromNameAndType(POSITIONS_STORE, QueryableStoreTypes.keyValueStore()));
            ReadOnlyKeyValueStore<String, Long> applications = streams.store(fromNameAndType(APPLICATIONS_STORE, QueryableStoreTypes.keyValueStore()));
            ReadOnlyKeyValueStore<String, Double> scoring = streams.store(fromNameAndType(SCORING_STORE, QueryableStoreTypes.keyValueStore()));

            long posCount = positions.get("count") != null ? positions.get("count") : 0L;
            long appCount = applications.get("count") != null ? applications.get("count") : 0L;
            double scoreAvg = scoring.get("average") != null ? scoring.get("average") : 0.0;
            double avgAppPerPos = posCount == 0 ? 0 : (double) appCount / posCount;

            BigDecimal roundedScoreAvg = new BigDecimal(scoreAvg).setScale(2, RoundingMode.HALF_UP);
            BigDecimal roundedAvgAppPerPos = new BigDecimal(avgAppPerPos).setScale(2, RoundingMode.HALF_UP);

            globalPositionDTO.setTotalPositions(posCount);
            globalPositionDTO.setAverageApplications(roundedAvgAppPerPos.doubleValue());
            globalPositionDTO.setAverageScore(roundedScoreAvg.doubleValue());
        }

        return Response.ok().entity(globalPositionDTO).build();
    }
}
