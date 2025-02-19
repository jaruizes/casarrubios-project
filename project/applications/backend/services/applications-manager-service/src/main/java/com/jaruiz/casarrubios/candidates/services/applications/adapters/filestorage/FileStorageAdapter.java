package com.jaruiz.casarrubios.candidates.services.applications.adapters.filestorage;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.jaruiz.casarrubios.candidates.services.applications.business.exceptions.ApplicationFileNotStoredException;
import com.jaruiz.casarrubios.candidates.services.applications.business.model.Application;
import com.jaruiz.casarrubios.candidates.services.applications.business.ports.FileStoragePort;
import com.jaruiz.casarrubios.candidates.services.applications.infrastructure.config.Config;
import io.minio.*;
import org.springframework.stereotype.Service;

@Service
public class FileStorageAdapter implements FileStoragePort {
    private final MinioClient minioClient;
    private final Config config;

    public FileStorageAdapter(MinioClient minioClient, Config config) {
        this.minioClient = minioClient;
        this.config = config;
    }

    @Override
    public String storeFile(Application application) throws ApplicationFileNotStoredException {
        final String bucketName = config.getBucketName();

        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            final InputStream inputStream = new ByteArrayInputStream(application.getCvFile());
            var response = minioClient.putObject(
                PutObjectArgs.builder()
                             .bucket(bucketName)
                             .object(application.getId().toString())
                             .stream(
                                 inputStream, inputStream.available(), -1)
                             .contentType("application/pdf")
                             .userMetadata(getMetadata(application))
                             .build());

            return response.object();
        } catch (Exception e) {
            throw new ApplicationFileNotStoredException(application.getId());
        }
    }

    public void deleteFile(String path) {
        try {
            RemoveObjectArgs args = RemoveObjectArgs.builder()
                                                    .bucket(config.getBucketName())
                                                    .object(path)
                                                    .build();
            minioClient.removeObject(args);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred: " + e.getMessage());
        }
    }

    private Map<String, String> getMetadata(Application application) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("name", application.getName());
        metadata.put("email", application.getEmail());
        metadata.put("phone", application.getPhone());
        metadata.put("positionId", String.valueOf(application.getPositionId()));
        return metadata;
    }
}
