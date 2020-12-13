package com.luoyk.osf.achieve.minio;

import com.luoyk.osf.core.definition.AbstractOsf;
import com.luoyk.osf.core.definition.achieve.FileAction;
import com.luoyk.osf.core.definition.achieve.PictureAction;

public class MinioOsf extends AbstractOsf {
    @Override
    protected FileAction fileActionProvider() {
        return null;
    }

    @Override
    protected PictureAction pictureActionProvider() {
        return null;
    }
}
