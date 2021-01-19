package com.luoyk.osf.achieve.mongodb;

import com.luoyk.osf.core.cache.OsfCache;
import com.luoyk.osf.core.definition.AbstractOsf;
import com.luoyk.osf.core.definition.achieve.FileAction;
import com.luoyk.osf.core.definition.achieve.PictureAction;
import com.luoyk.osf.core.exception.OsfException;
import com.luoyk.osf.core.mq.OsfSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.InputStream;
import java.util.logging.Logger;

public class MongoDBOsf extends AbstractOsf {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private GridFsTemplate gridFsTemplate;

    private OsfCache osfCache;

    private OsfSender osfSender;

    @Override
    protected FileAction fileActionProvider() {
        return new FileAction() {
            @Override
            public String saveTemp(String filename, InputStream inputStream) {
                return null;
            }

            @Override
            public String transferFile(String tempId) {
                return null;
            }

            @Override
            public String transferReplace(String tempId, String oldFile) {
                return null;
            }

            @Override
            public boolean delete(String file) {
                return false;
            }

            @Override
            public boolean deleteTemp(String tempPath) {
                return false;
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

    public void setOsfCache(OsfCache osfCache) {
        this.osfCache = osfCache;
    }

    public void setOsfSender(OsfSender osfSender) {
        this.osfSender = osfSender;
    }
}
