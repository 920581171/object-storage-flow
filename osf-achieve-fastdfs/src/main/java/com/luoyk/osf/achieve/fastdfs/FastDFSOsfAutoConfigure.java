package com.luoyk.osf.achieve.fastdfs;

import com.luoyk.osf.autoconfigure.definition.AbstractOsfAutoConfigure;
import com.luoyk.osf.core.definition.Osf;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FastDFSOsfAutoConfigure extends AbstractOsfAutoConfigure {
    @Override
    public Osf abstractOsfProvider() {
        return new FastDFSOsf();
    }
}
