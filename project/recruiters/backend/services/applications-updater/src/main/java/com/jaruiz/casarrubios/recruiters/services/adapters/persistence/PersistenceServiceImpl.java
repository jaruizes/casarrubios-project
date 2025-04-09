package com.jaruiz.casarrubios.recruiters.services.adapters.persistence;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

import com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository.ApplicationsRepository;
import com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository.CandidatesRepository;
import com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository.entities.ApplicationEntity;
import com.jaruiz.casarrubios.recruiters.services.adapters.persistence.repository.entities.CandidateEntity;
import com.jaruiz.casarrubios.recruiters.services.business.model.Application;
import com.jaruiz.casarrubios.recruiters.services.business.model.Candidate;
import com.jaruiz.casarrubios.recruiters.services.business.ports.PersistenceService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PersistenceServiceImpl implements PersistenceService {

    private static final Logger logger = Logger.getLogger(PersistenceServiceImpl.class);
    private final ApplicationsRepository applicationsRepository;
    private final CandidatesRepository candidatesRepository;

    public PersistenceServiceImpl(ApplicationsRepository applicationsRepository, CandidatesRepository candidatesRepository) {
        this.applicationsRepository = applicationsRepository;
        this.candidatesRepository = candidatesRepository;
    }

    @Transactional
    @Override
    public void saveApplication(Application application) {
        logger.info("Saving application into database with Id: " + application.getId());

        String candidateEmail = application.getCandidate().getEmail();
        final Optional<CandidateEntity> currentCandidateEntity = this.candidatesRepository.findByEmail(candidateEmail);
        UUID candidateId = null;
        if (currentCandidateEntity.isEmpty()) {
            logger.info("New candidate detected. Saving candidate with email: " + candidateEmail);

            final CandidateEntity candidateEntity = mapToCandidateEntity(application.getCandidate());
            this.candidatesRepository.persist(candidateEntity);
            candidateId = candidateEntity.getId();
            logger.info("Candidate with email: " + candidateEmail + " saved into database (UUID: " + candidateId + ")");
        } else {
            candidateId = currentCandidateEntity.get().getId();
        }

        final ApplicationEntity applicationEntity = mapToEntity(application, candidateId);
        this.applicationsRepository.persist(applicationEntity);
        application.getCandidate().setId(candidateId);

        logger.info("Application with Id: " + application.getId() + " saved into database");
    }


    private ApplicationEntity mapToEntity(Application application, UUID candidateId) {
        final var createdAt = getTimestamp(application.getCreatedAt());

        ApplicationEntity applicationEntity = new ApplicationEntity();
        applicationEntity.setId(application.getId());
        applicationEntity.setPositionId(application.getPositionId());
        applicationEntity.setCandidateId(candidateId);

        applicationEntity.setCreatedAt(createdAt);
        return applicationEntity;
    }

    private CandidateEntity mapToCandidateEntity(Candidate candidate) {
        final var createdAt = getTimestamp(candidate.getCreatedAt());

        CandidateEntity candidateEntity = new CandidateEntity();
        candidateEntity.setName(candidate.getName());
        candidateEntity.setEmail(candidate.getEmail());
        candidateEntity.setPhone(candidate.getPhone());
        candidateEntity.setCv(candidate.getCv());
        candidateEntity.setCreatedAt(createdAt);

        return candidateEntity;
    }

    private Candidate mapEntityToCandidate(CandidateEntity candidateEntity) {
        return new Candidate(candidateEntity.getName(),
            candidateEntity.getEmail(),
            candidateEntity.getPhone(),
            candidateEntity.getCv(),
            candidateEntity.getCreatedAt().getTime());
    }

    private static Timestamp getTimestamp(long epoch) {
        int length = String.valueOf(epoch).length();
        long epochMilis = epoch;

        if (length <= 10) {
            epochMilis = epoch * 1_000;
        } else if (length >= 16) {
            epochMilis = epoch / 1_000;
        }

        return new Timestamp(epochMilis);
    }
}
