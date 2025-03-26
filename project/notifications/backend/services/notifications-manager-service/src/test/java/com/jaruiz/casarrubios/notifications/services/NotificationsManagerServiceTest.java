package com.jaruiz.casarrubios.notifications.services;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import com.jaruiz.casarrubios.notifications.services.adapters.messaging.dto.NotificationDTO;
import com.jaruiz.casarrubios.notifications.services.business.model.NotificationType;
import com.jaruiz.casarrubios.notifications.services.util.containers.KafkaContainer;
import com.jaruiz.casarrubios.notifications.services.util.kafka.EventsProducer;
import com.jaruiz.casarrubios.notifications.services.util.kafka.KafkaAdminHelper;
import com.jaruiz.casarrubios.notifications.services.util.kafka.NotificationsConsumer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(KafkaContainer.class)
public class NotificationsManagerServiceTest {

    private static final Logger logger = Logger.getLogger(NotificationsManagerServiceTest.class);

    @Inject EventsProducer eventsProducer;
    @Inject NotificationsConsumer notificationsConsumer;

    @Test
    void givenAnApplicationScoredEvent_whenTheScoreIsGreaterThan60_thenANotificationIsSent() throws IOException {
        eventsProducer.publishApplicationBadScored();
        eventsProducer.publishApplicationGoodScored();

        Awaitility.await()
                  .atMost(Duration.ofSeconds(20))
                  .until(() -> notificationsConsumer.isNotificationPublished());

        assertEquals(1, notificationsConsumer.getNotificationsDTO().size());
        NotificationDTO notificationDTO = notificationsConsumer.getNotificationsDTO().getFirst();
        assertNotNull(notificationDTO);
        assertNotNull(notificationDTO.getApplicationId());
        assertEquals("da3ad6a7-51c5-464e-a67f-557964dbf889", notificationDTO.getApplicationId());
        assertNotNull(notificationDTO.getPositionId());
        assertEquals(10L, notificationDTO.getPositionId());
        assertEquals(NotificationType.GOOD_SCORING.toString(), notificationDTO.getType());
        assertNotNull(notificationDTO.getData());
        Map<String, Object> data = notificationDTO.getData();
        assertEquals(1, data.size());
        assertNotNull(data.get("score"));
        assertEquals(0.85, data.get("score"));
    }
}
