package com.luoyk.osf.achieve.mongodb;

import com.luoyk.osf.core.cache.OsfCache;
import com.luoyk.osf.core.definition.AbstractOsf;
import com.luoyk.osf.core.definition.achieve.FileAction;
import com.luoyk.osf.core.definition.achieve.PictureAction;
import com.luoyk.osf.core.exception.OsfException;
import com.luoyk.osf.core.mq.DelayMessage;
import com.luoyk.osf.core.mq.MessageHandler;
import com.luoyk.osf.core.mq.OsfSender;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.logging.Logger;

public class MongoDBOsf extends AbstractOsf implements MessageHandler {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private GridFsTemplate gridFsTemplate;

    private OsfCache osfCache;

    private OsfSender osfSender;

    @Override
    protected FileAction fileActionProvider() {
        return new FileAction() {
            @Override
            public String saveTemp(String filename, InputStream inputStream) {
                final String mimeType = getMimeType(filename);
                final String suffix = getFileSuffixName(filename);
                final String tempUuid = getTempUuid();
                final String tempName = concatFilename(getTempUuid(), suffix);

                InputStream stream = isImage(filename) ?
                        new ByteArrayInputStream(compressImage(inputStream, suffix)) :
                        inputStream;

                gridFsTemplate.store(stream, tempName, mimeType);
                osfCache.newTempMap(tempUuid, tempName);
                osfSender.send(DelayMessage.newFileDelayMessage(tempUuid));

                return tempUuid;
            }

            @Override
            public String transferFile(String tempId) {
                final String path = osfCache.getPathByTempId(tempId)
                        .orElseThrow(() -> new OsfException("Temp file expired"));
                osfCache.removeTempId(tempId);
                return path;
            }

            @Override
            public String transferReplace(String tempId, String oldFile) {
                String path = transferFile(tempId);
                delete(oldFile);
                return path;
            }

            @Override
            public boolean delete(String file) {
                Query query = Query.query(GridFsCriteria.whereFilename().is(file));
                final GridFSFile one = gridFsTemplate.findOne(query);
                if (one != null) {
                    gridFsTemplate.delete(query);
                    logger.info("Delete file success: " + file);
                    return true;
                }
                logger.warning("Delete file fail:" + file);
                return false;
            }

            @Override
            public boolean deleteTemp(String tempPath) {
                return delete(tempPath);
            }
        };
    }

    @Override
    protected PictureAction pictureActionProvider() {
        return new PictureAction() {
            @Override
            protected String saveTempProvider(String filename, InputStream inputStream) {
                return null;
            }

            @Override
            protected String transferFileProvider(String tempId) {
                return null;
            }

            @Override
            protected boolean deleteProvider(String file) {
                return false;
            }

            @Override
            protected boolean deleteTempProvider(String file) {
                return false;
            }

            @Override
            protected void makeThumbnail(String tempId, String filename, InputStream inputStream) throws OsfException {

            }

            @Override
            protected void transferThumbnail(String tempId) throws OsfException {

            }

            @Override
            protected void deleteThumbnail(String file) throws OsfException {

            }

            @Override
            protected void deleteTempThumbnail(String file) throws OsfException {

            }

            @Override
            public String transferReplace(String tempId, String oldFile) {
                return null;
            }
        };
    }

    @Autowired
    public void setGridFsTemplate(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    @Autowired
    public void setOsfCache(OsfCache osfCache) {
        this.osfCache = osfCache;
    }

    @Autowired
    public void setOsfSender(OsfSender osfSender) {
        this.osfSender = osfSender;
    }

    @Override
    public boolean handler(DelayMessage delayMessage) {
        final String tempUuid = delayMessage.getTempPath();
        final Optional<String> pathByTempId = osfCache.getPathByTempId(tempUuid);
        logger.info("Receive handler by mongo: " + delayMessage.toString());

        if (pathByTempId.isPresent()) {
            switch (delayMessage.getFileType()) {
                case FILE:
                    return this.fileActionProvider().deleteTemp(pathByTempId.get());
                case PICTURE:
                    return this.getPictureAction().deleteTemp(pathByTempId.get());
            }
        }

        logger.info("Delay delete temp non present，tempUuid: " + tempUuid);
        return false;
    }
}
