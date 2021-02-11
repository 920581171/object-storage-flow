package com.luoyk.osf.core.helper;

import com.luoyk.osf.core.definition.achieve.AbstractOsf;
import com.luoyk.osf.core.definition.achieve.FileAction;
import com.luoyk.osf.core.definition.achieve.PictureAction;

public class OsfTemplate extends OsfHelper {

    public OsfTemplate(AbstractOsf abstractOsf) {
        super(abstractOsf);
    }

    public FileAction file() {
        return ((AbstractOsf) osf).getFileAction();
    }

    public PictureAction picture() {
        return ((AbstractOsf) osf).getPictureAction();
    }

    @Override
    public AbstractOsf getOsf() {
        return (AbstractOsf) super.getOsf();
    }
}
