package com.luoyk.osf.achieve.minio;

import com.luoyk.osf.core.cache.OsfCache;
import com.luoyk.osf.core.definition.achieve.AbstractOsf;
import com.luoyk.osf.core.definition.achieve.FileAction;
import com.luoyk.osf.core.definition.achieve.PictureAction;
import com.luoyk.osf.core.exception.OsfException;
import com.luoyk.osf.core.mq.DelayMessage;
import com.luoyk.osf.core.mq.OsfSender;
import io.minio.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

public class MinioOsf extends AbstractOsf {

    public final String tempBucket;

    public final String fileBucket;

    private OsfCache osfCache;

    private OsfSender osfSender;

    private MinioClient minioClient;

    public MinioOsf(String tempBucket, String fileBucket) {
        this.tempBucket = tempBucket;
        this.fileBucket = fileBucket;
    }

    @Override
    protected FileAction fileActionProvider() {
        return new FileAction() {
            @Override
            public String saveTemp(String filename, InputStream inputStream) {
                try {
                    final String mimeType = getMimeType(filename);
                    final String suffix = getFileSuffixName(filename);
                    final String tempUuid = getTempUuid();
                    final String tempName = concatFilename(getTempUuid(), suffix);

                    InputStream stream = isImage(filename) ?
                            new ByteArrayInputStream(compressImage(inputStream, suffix)) :
                            inputStream;
                    minioClient.putObject(PutObjectArgs.builder()
                            .bucket(tempBucket)
                            .contentType(mimeType)
                            .object(tempName)
                            .stream(stream, stream.available(), -1)
                            .build());

                    osfCache.newCountDownTempMap(tempUuid, tempName);
                    osfSender.send(DelayMessage.newFileDelayMessage(tempName));

                    return tempUuid;
                } catch (Exception e) {
                    throw new OsfException(e);
                }
            }

            @Override
            public String transferFile(String tempId) {
                try {
                    String tempName = osfCache.getCountDownPathByTempId(tempId).orElseThrow(
                            () -> new OsfException("Temp file expired")
                    );

                    minioClient.copyObject(CopyObjectArgs.builder()
                            .source(CopySource.builder()
                                    .bucket(tempBucket)
                                    .object(tempName)
                                    .build())
                            .bucket(fileBucket)
                            .metadataDirective(Directive.COPY)
                            .taggingDirective(Directive.COPY)
                            .object(tempName)
                            .build());

                    return '/' + fileBucket + '/' + tempName;
                } catch (Exception e) {
                    throw new OsfException(e);
                }
            }

            @Override
            public String transferReplace(String tempId, String oldFile) {
                String path = transferFile(tempId);
                if (!delete(oldFile)) {
                    logger.warning("Delete old file fail: " + oldFile);
                }
                return path;
            }

            @Override
            public boolean delete(String file) {

                String[] paths = file.split("/");

                if (!fileBucket.equals(paths[1])) {
                    return false;
                }

                try {
                    minioClient.removeObject(RemoveObjectArgs.builder()
                            .bucket(fileBucket)
                            .object(paths[2])
                            .build());
                    logger.info("Delete success: " + file);
                    return true;
                } catch (Exception e) {
                    logger.severe(e.getMessage());
                    return false;
                }
            }

            @Override
            public boolean deleteTemp(String tempPath) {
                try {
                    minioClient.removeObject(RemoveObjectArgs.builder()
                            .object(tempPath)
                            .bucket(tempBucket)
                            .build());
                    logger.info("DeleteTemp success: " + tempPath);
                    return true;
                } catch (Exception e) {
                    logger.severe(e.getMessage());
                    return false;
                }
            }
        };
    }

    @Override
    protected PictureAction pictureActionProvider() {
        return new PictureAction() {
            @Override
            protected String saveTempProvider(String filename, InputStream inputStream) {
                try {
                    final String mimeType = getMimeType(filename);
                    final String suffix = getFileSuffixName(filename);
                    final String tempUuid = getTempUuid();
                    final String tempName = concatFilename(getTempUuid(), suffix);

                    if (!isImage(filename)) {
                        throw new OsfException("Not a image " + filename);
                    }

                    InputStream stream = new ByteArrayInputStream(compressImage(inputStream, suffix));

                    minioClient.putObject(PutObjectArgs.builder()
                            .bucket(tempBucket)
                            .contentType(mimeType)
                            .object(tempName)
                            .stream(stream, stream.available(), -1)
                            .build());

                    osfCache.newCountDownTempMap(tempUuid, tempName);
                    osfSender.send(DelayMessage.newPictureDelayMessage(tempName));

                    return tempUuid;
                } catch (Exception e) {
                    throw new OsfException(e.getMessage());
                }
            }

            @Override
            protected String transferFileProvider(String tempId) {
                return getFileAction().transferFile(tempId);
            }

            @Override
            protected boolean deleteProvider(String file) {
                return getFileAction().delete(file);
            }

            @Override
            protected boolean deleteTempProvider(String file) {
                return getFileAction().deleteTemp(file);
            }

            @Override
            protected void makeThumbnail(String tempId, String filename, InputStream inputStream) throws OsfException {
                String suffix = getFileSuffixName(filename);
                String mimeType = getMimeType(filename);

                final Optional<String> tempNameOpt = osfCache.getCountDownPathByTempId(tempId);
                if (!tempNameOpt.isPresent()) {
                    throw new OsfException("Does not exist tempPath by tempId:" + tempId);
                }

                ByteArrayInputStream stream;

                for (PictureSizeEnum value : PictureSizeEnum.values()) {
                    String thumbnailName = value.fixName() + "/" + tempNameOpt.get();
                    try {
                        inputStream.reset();
                        stream = new ByteArrayInputStream(resizeImage(inputStream, suffix, value.size(), value.size()));
                        minioClient.putObject(PutObjectArgs.builder()
                                .contentType(mimeType)
                                .bucket(tempBucket)
                                .object(thumbnailName)
                                .stream(stream, stream.available(), -1)
                                .build());
                    } catch (Exception e) {
                        logger.warning("makeThumbnail error: " + thumbnailName + "\n" + e.getMessage());
                    }
                }

            }

            @Override
            protected void transferThumbnail(String tempId) throws OsfException {
                final Optional<String> tempNameOpt = osfCache.getCountDownPathByTempId(tempId);
                if (!tempNameOpt.isPresent()) {
                    throw new OsfException("Does not exist tempPath by tempId:" + tempId);
                }

                for (PictureSizeEnum value : PictureSizeEnum.values()) {
                    String thumbnailName = value.fixName() + "/" + tempNameOpt.get();
                    try {
                        minioClient.copyObject(CopyObjectArgs.builder()
                                .source(CopySource.builder()
                                        .object(thumbnailName)
                                        .bucket(tempBucket)
                                        .build())
                                .object(thumbnailName)
                                .bucket(fileBucket)
                                .taggingDirective(Directive.COPY)
                                .metadataDirective(Directive.COPY)
                                .build());
                    } catch (Exception e) {
                        logger.warning("transferThumbnail error: " + thumbnailName + "\n" + e.getMessage());
                    }
                }
            }

            @Override
            protected void deleteThumbnail(String file) throws OsfException {
                String[] paths = file.split("/");

                if (!fileBucket.equals(paths[1])) {
                    logger.warning("DeleteThumbnail fail, file path is: " + file);
                }

                if (fileBucket.equals(paths[1])) {
                    for (PictureSizeEnum value : PictureSizeEnum.values()) {
                        String thumbnailName = value.fixName() + "/" + paths[2];
                        try {
                            minioClient.removeObject(RemoveObjectArgs.builder()
                                    .object(thumbnailName)
                                    .bucket(fileBucket)
                                    .build());
                            logger.info("DeleteThumbnail success: " + file);
                        } catch (Exception e) {
                            logger.warning("DeleteThumbnail error:" + thumbnailName + "\n" + e.getMessage());
                        }
                    }
                }
            }

            @Override
            protected void deleteTempThumbnail(String file) throws OsfException {
                for (PictureSizeEnum value : PictureSizeEnum.values()) {
                    String thumbnailName = value.fixName() + "/" + file;
                    try {
                        minioClient.removeObject(RemoveObjectArgs.builder()
                                .object(thumbnailName)
                                .bucket(tempBucket)
                                .build());
                        logger.info("DeleteTempThumbnail success: " + file);
                    } catch (Exception e) {
                        logger.warning("DeleteTempThumbnail error:" + thumbnailName + "\n" + e.getMessage());
                    }
                }
            }

            @Override
            public String transferReplace(String tempId, String oldFile) {
                String path = transferFile(tempId);
                if (!delete(oldFile)) {
                    logger.warning("Delete old file fail: " + oldFile);
                }
                return path;
            }
        };
    }

    @Autowired
    public MinioOsf setOsfCache(OsfCache osfCache) {
        this.osfCache = osfCache;
        return this;
    }

    @Autowired
    public void setMinioClient(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Autowired
    public void setOsfSender(OsfSender osfSender) {
        this.osfSender = osfSender;
    }
}
