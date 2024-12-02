package com.jaruiz.casarrubios.candidates.services.applications.adapters.filestorage;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

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
    public String storeFile(Application application) {
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
                             .build());

            return response.object();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred: " + e.getMessage());
        }
    }

    public void deleteFile(String path) {
        final String bucketName = config.getBucketName();
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
}
