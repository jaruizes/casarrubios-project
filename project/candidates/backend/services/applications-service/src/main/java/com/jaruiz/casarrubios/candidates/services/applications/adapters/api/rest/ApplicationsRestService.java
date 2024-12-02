package com.jaruiz.casarrubios.candidates.services.applications.adapters.api.rest;

import java.io.IOException;
import java.util.UUID;

import com.jaruiz.casarrubios.candidates.services.applications.business.ApplicationsService;
import com.jaruiz.casarrubios.candidates.services.applications.business.exceptions.ApplicationIncompleteException;
import com.jaruiz.casarrubios.candidates.services.applications.business.model.Application;
import com.jaruiz.casarrubios.candidates.services.applications.business.model.Candidate;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.api.rest.dto.ApplicationResponseDTO;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.api.rest.dto.CandidateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ApplicationsRestService implements ApplicationsApi {

    private final ApplicationsService applicationsService;

    public ApplicationsRestService(ApplicationsService applicationsService) {
        this.applicationsService = applicationsService;
    }

    @Override public ResponseEntity<ApplicationResponseDTO> uploadCV(CandidateDTO candidate, Long positionId, MultipartFile cvFile) {
        try {
            final Application application = mapParamsToApplication(mapCandidateDTOToCandidate(candidate), positionId, cvFile);

            final UUID applicationSavedId = applicationsService.uploadCV(application);

            return new ResponseEntity<>(mapApplicationToApplicationResponseDTO(positionId, applicationSavedId), HttpStatus.CREATED);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        } catch (ApplicationIncompleteException e) {
            throw new RuntimeException(e);
        }

    }

    private Candidate mapCandidateDTOToCandidate(CandidateDTO candidateDTO) {
        return new Candidate(candidateDTO.getName(),
                            candidateDTO.getSurname(),
                            candidateDTO.getEmail(),
                            candidateDTO.getPhone());
    }

    private Application mapParamsToApplication(Candidate candidate, Long positionId, MultipartFile cvFile) throws IOException {
        return new Application(candidate, cvFile.getBytes(), positionId);
    }

    private ApplicationResponseDTO mapApplicationToApplicationResponseDTO(Long positionId, UUID applicationSavedId) {
        final ApplicationResponseDTO responseDTO = new ApplicationResponseDTO();
        responseDTO.setApplicationId(applicationSavedId.toString());
        responseDTO.setPosition(positionId);

        return responseDTO;
    }
}
