package com.luoyk.osf.autoconfigure.common;

import com.luoyk.osf.core.cache.OsfCache;
import com.luoyk.osf.core.definition.AbstractOsf;

public interface OsfAutoConfigure {

    AbstractOsf abstractOsf();

    OsfCache osfCache();

}
