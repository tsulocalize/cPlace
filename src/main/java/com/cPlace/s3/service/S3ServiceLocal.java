package com.cPlace.s3.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("local")
public class S3ServiceLocal implements S3Service {
    @Override
    public void uploadData(byte[] data) {
    }
}
