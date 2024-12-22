package com.jaruiz.casarrubios.recruiters.services;

import com.jaruiz.casarrubios.recruiters.services.posmanager.api.rest.beans.PositionDTO;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class PosManagerIT {

    @Test
    public void givenSomePositions_whenGetAllPositions_thenListOfPositionsIsReturned() {
        final Response response = given()
            .when().get("/positions");

        final PositionDTO[] positions = response.getBody().as(PositionDTO[].class);
        assertNotNull(positions);
        assertEquals(2, positions.length);
        for (PositionDTO position : positions) {
            checkPosition(position);
        }
    }

    private void checkPosition(PositionDTO position) {
        assertNotNull(position);
        assertNotNull(position.getId());
        assertNotNull(position.getTitle());
        assertNotNull(position.getDescription());
    }

}
