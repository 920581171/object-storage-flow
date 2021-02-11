package com.luoyk.osf.core.helper;

import com.luoyk.osf.core.definition.Osf;
import com.luoyk.osf.core.definition.achieve.AbstractOsf;

/**
 * 操作帮助类
 *
 * @author luoyk
 */
public abstract class OsfHelper {

    protected final Osf osf;

    public OsfHelper(Osf osf) {
        this.osf = osf;
    }

    public Osf getOsf() {
        return osf;
    }
}
