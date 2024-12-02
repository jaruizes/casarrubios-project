package com.jaruiz.casarrubios.candidates.services.applications.business.model;

import java.util.UUID;

import okio.Path;

public class Application {

    private final UUID id;
    private final byte[] cvFile;
    private final Candidate candidate;
    private final Long positionId;

    public Application(Candidate candidate, byte[] cvFile, Long positionId) {
        this.id = UUID.randomUUID();
        this.candidate = candidate;
        this.cvFile = cvFile;
        this.positionId = positionId;
    }

    public boolean isComplete() {
        return candidate.isComplete() && (cvFile != null && cvFile.length > 0) && positionId != null;
    }

    public String getFilePath() {
        return String.valueOf(getPositionId())
                     .concat(Path.DIRECTORY_SEPARATOR)
                     .concat(getId().toString());
    }

    public UUID getId() {
        return id;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public Long getPositionId() {
        return positionId;
    }

    public String getName() {
        return candidate.getName();
    }

    public String getSurname() {
        return candidate.getSurname();
    }

    public String getPhone() {
        return candidate.getPhone();
    }

    public String getEmail() {
        return candidate.getEmail();
    }

    public byte[] getCvFile() {
        return cvFile;
    }
}
