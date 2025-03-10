package com.jaruiz.casarrubios.recruiters.services.applications.adapters.fileStorage;

import java.util.UUID;

import com.jaruiz.casarrubios.recruiters.services.applications.business.exceptions.CVNotFoundException;
import com.jaruiz.casarrubios.recruiters.services.applications.business.ports.CVServicePort;
import com.jaruiz.casarrubios.recruiters.services.applications.infrastructure.Config;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import org.springframework.stereotype.Service;

@Service
public class MinioCVServiceAdapter implements CVServicePort {
    private final MinioClient minioClient;
    private final Config config;

    public MinioCVServiceAdapter(MinioClient minioClient, Config config) {
        this.minioClient = minioClient;
        this.config = config;
    }

    @Override
    public byte[] getCV(UUID applicationId) throws CVNotFoundException {
        try {
            final GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder()
                                                                                  .bucket(config.getBucketName())
                                                                                  .object(applicationId.toString()).build());
            return response.readAllBytes();
        } catch (Exception e) {
            throw new CVNotFoundException(applicationId);
        }
    }
}
