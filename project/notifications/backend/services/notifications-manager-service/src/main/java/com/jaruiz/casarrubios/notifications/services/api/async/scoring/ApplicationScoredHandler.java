package com.jaruiz.casarrubios.notifications.services.api.async.scoring;

import com.jaruiz.casarrubios.notifications.services.api.async.scoring.dto.ApplicationScoredEventDTO;
import com.jaruiz.casarrubios.notifications.services.business.NotificationsManagerService;
import com.jaruiz.casarrubios.notifications.services.business.model.ApplicationScore;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ApplicationScoredHandler {
    private static final Logger logger = Logger.getLogger(ApplicationScoredHandler.class);
    private final NotificationsManagerService notificationsManagerService;

    public ApplicationScoredHandler(NotificationsManagerService notificationsManagerService) {
        this.notificationsManagerService = notificationsManagerService;
    }


    @Incoming("application-scored")
    public void handler(ApplicationScoredEventDTO applicationScoredEventDTO) {
        logger.info("Received application scored for application (" + applicationScoredEventDTO.getApplicationId() + ")");

        this.notificationsManagerService.evaluateApplicationScore(mapToApplicationScore(applicationScoredEventDTO));
    }

    private ApplicationScore mapToApplicationScore(ApplicationScoredEventDTO applicationScoredEventDTO) {
        return new ApplicationScore(applicationScoredEventDTO.getApplicationId(),
                                    applicationScoredEventDTO.getPositionId(),
                                    applicationScoredEventDTO.getScoring().getScore());
    }
}
