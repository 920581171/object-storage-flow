package com.luoyk.osf.achieve.minio;

import com.luoyk.osf.autoconfigure.definition.AbstractOsfAutoConfigure;
import com.luoyk.osf.core.definition.AbstractOsf;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

@Configuration
public class MinioOsfAutoConfigure extends AbstractOsfAutoConfigure {

    private static final Logger logger = Logger.getLogger(MinioOsfAutoConfigure.class.getName());

    public static String tempBucket;

    public static String fileBucket;

    @Override
    public AbstractOsf abstractOsfProvider() {
        return new MinioOsf(tempBucket, fileBucket);
    }

    @Bean
    public MinioClient minioClient() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {

        final String endpoint = getEnvironment().getProperty("osf.minio.endpoint");
        final String accessKey = getEnvironment().getProperty("osf.minio.accessKey");
        final String secretKey = getEnvironment().getProperty("osf.minio.secretKey");

        if (StringUtils.isEmpty(endpoint)) {
            throw new RuntimeException("'osf.minio.endpoint' can not be empty");
        }
        if (StringUtils.isEmpty(accessKey)) {
            throw new RuntimeException("'osf.minio.accessKey' can not be empty");
        }
        if (StringUtils.isEmpty(secretKey)) {
            throw new RuntimeException("'osf.minio.secretKey' can not be empty");
        }

        final MinioClient minioClient = MinioClient.builder().endpoint(endpoint)
                .credentials(accessKey, secretKey).build();

        final String bucketName = getEnvironment().getProperty("osf.minio.bucketName");

        if (StringUtils.isEmpty(bucketName)) {
            tempBucket = "osf-temp";
            fileBucket = "osf";
        } else {
            tempBucket = bucketName + "-temp";
            fileBucket = bucketName;
        }

        if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(tempBucket).build())) {
            logger.info("Bucket " + tempBucket + " exists");
        } else {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(tempBucket).build());
            logger.info("Create bucket " + tempBucket + " success");
        }

        if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(fileBucket).build())) {
            logger.info("Bucket " + fileBucket + " exists");
        } else {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(fileBucket).build());
            logger.info("Create bucket " + fileBucket + " success");
        }

        return minioClient;
    }
}
