package com.luoyk.osf.core.helper;

import com.luoyk.osf.core.definition.achieve.AbstractOsf;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class OsfMultipleTemplate {

    private final List<OsfTemplate> osfTemplates;

    public OsfMultipleTemplate() {
        this.osfTemplates = new LinkedList<>();
    }

    public void addTemplate(OsfTemplate osfTemplate) {
        osfTemplates.add(osfTemplate);
    }

    public class MultipleFileAction {
        public String[] saveTempMultiple(String filename, InputStream inputStream) {
            return (String[]) osfTemplates.stream().map(OsfTemplate::getOsf)
                    .map(AbstractOsf::getFileAction)
                    .map(fileAction -> fileAction.saveTemp(filename, inputStream)).toArray();
        }

        public String[] transferFileMultiple(String tempId) {
            return (String[]) osfTemplates.stream().map(OsfTemplate::getOsf)
                    .map(AbstractOsf::getFileAction)
                    .map(fileAction -> fileAction.transferFile(tempId)).toArray();
        }

        public String[] transferReplaceMultiple(String tempId, String oldFile) {
            return (String[]) osfTemplates.stream().map(OsfTemplate::getOsf)
                    .map(AbstractOsf::getFileAction)
                    .map(fileAction -> fileAction.transferReplace(tempId, oldFile)).toArray();
        }

        public Boolean[] deleteMultiple(String file) {
            return (Boolean[]) osfTemplates.stream().map(OsfTemplate::getOsf)
                    .map(AbstractOsf::getFileAction)
                    .map(fileAction -> fileAction.delete(file)).toArray();
        }

        public Boolean[] deleteTempMultiple(String tempPath) {
            return (Boolean[]) osfTemplates.stream().map(OsfTemplate::getOsf)
                    .map(AbstractOsf::getFileAction)
                    .map(fileAction -> fileAction.deleteTemp(tempPath)).toArray();
        }
    }

    public class MultiplePictureAction {
        public String[] saveTempMultiple(String filename, InputStream inputStream) {
            return (String[]) osfTemplates.stream().map(OsfTemplate::getOsf)
                    .map(AbstractOsf::getPictureAction)
                    .map(pictureAction -> pictureAction.saveTemp(filename, inputStream)).toArray();
        }

        public String[] transferFileMultiple(String tempId) {
            return (String[]) osfTemplates.stream().map(OsfTemplate::getOsf)
                    .map(AbstractOsf::getPictureAction)
                    .map(pictureAction -> pictureAction.transferFile(tempId)).toArray();
        }

        public String[] transferReplaceMultiple(String tempId, String oldFile) {
            return (String[]) osfTemplates.stream().map(OsfTemplate::getOsf)
                    .map(AbstractOsf::getPictureAction)
                    .map(pictureAction -> pictureAction.transferReplace(tempId, oldFile)).toArray();
        }

        public Boolean[] deleteMultiple(String file) {
            return (Boolean[]) osfTemplates.stream().map(OsfTemplate::getOsf)
                    .map(AbstractOsf::getPictureAction)
                    .map(pictureAction -> pictureAction.delete(file)).toArray();
        }

        public Boolean[] deleteTempMultiple(String tempPath) {
            return (Boolean[]) osfTemplates.stream().map(OsfTemplate::getOsf)
                    .map(AbstractOsf::getPictureAction)
                    .map(pictureAction -> pictureAction.deleteTemp(tempPath)).toArray();
        }
    }
}
