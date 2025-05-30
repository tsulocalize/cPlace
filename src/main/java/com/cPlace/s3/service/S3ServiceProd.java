package com.cPlace.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Profile("prod")
@RequiredArgsConstructor
public class S3ServiceProd implements S3Service {

    @Value("${aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 s3;
    private final Clock clock;

    private static final Queue<byte[]> QUEUE = new ConcurrentLinkedQueue<>();
    private static final String CONTENT_TYPE = "application/octet-stream";

    @Override
    public void uploadData(byte[] data) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(data.length);
        objectMetadata.setContentType(CONTENT_TYPE);

        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucket,
                getTimeKey(),
                new ByteArrayInputStream(data),
                objectMetadata);

        s3.putObject(putObjectRequest);
    }

    // yyyy-mm-ddTHH-mm
    private String getTimeKey() {
        return LocalDateTime.now(clock).withSecond(0).toString();
    }
}
