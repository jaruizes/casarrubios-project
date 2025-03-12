package com.jaruiz.casarrubios.recruiters.services.api.output.async;

import java.util.concurrent.CompletionStage;

import com.jaruiz.casarrubios.recruiters.services.api.output.async.dto.NewApplicationReceivedDTO;
import com.jaruiz.casarrubios.recruiters.services.business.model.Application;
import com.jaruiz.casarrubios.recruiters.services.business.ports.NewApplicationReceivedEventPublisherPort;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

@ApplicationScoped
public class NewApplicationReceivedEventPublisher implements NewApplicationReceivedEventPublisherPort {
    private static final Logger logger = Logger.getLogger(NewApplicationReceivedEventPublisher.class);

    @Inject
    @Channel("applications-received-out") Emitter<NewApplicationReceivedDTO> newApplicationReceivedEmitter;

    @Transactional
    @Override
    public void publishNewApplicationReceivedEvent(Application application) {
        NewApplicationReceivedDTO newApplicationReceivedEvent = mapToNewApplicationReceivedDTO(application);
        OutgoingKafkaRecordMetadata<?> metadata = OutgoingKafkaRecordMetadata.builder().withKey(application.getId().toString()).build();

        newApplicationReceivedEmitter.send(Message.of(newApplicationReceivedEvent).addMetadata(metadata));
    }

    private NewApplicationReceivedDTO mapToNewApplicationReceivedDTO(Application application) {
        final NewApplicationReceivedDTO newApplicationReceivedDTO = new NewApplicationReceivedDTO();
        newApplicationReceivedDTO.setId(application.getId());
        newApplicationReceivedDTO.setName(application.getName());
        newApplicationReceivedDTO.setEmail(application.getEmail());
        newApplicationReceivedDTO.setPhone(application.getPhone());
        newApplicationReceivedDTO.setCv(application.getCv());
        newApplicationReceivedDTO.setPositionId(application.getPositionId());
        newApplicationReceivedDTO.setCreatedAt(application.getCreatedAt());


        return newApplicationReceivedDTO;
    }

}
