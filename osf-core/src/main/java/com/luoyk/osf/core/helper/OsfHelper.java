package com.luoyk.osf.core.helper;

import com.luoyk.osf.core.definition.AbstractOsf;
import com.luoyk.osf.core.definition.achieve.FileAction;
import com.luoyk.osf.core.definition.achieve.PictureAction;

/**
 * 操作帮助类
 *
 * @author luoyk
 */
public abstract class OsfHelper {

    private final AbstractOsf abstractOsf;

    public OsfHelper(AbstractOsf abstractOsf) {
        this.abstractOsf = abstractOsf;
    }

    public FileAction file() {
        return abstractOsf.getFileAction();
    }

    public PictureAction picture() {
        return abstractOsf.getPictureAction();
    }

}
