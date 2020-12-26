package com.luoyk.osf.achieve.minio;

import com.luoyk.osf.autoconfigure.definition.AbstractOsfAutoConfigure;
import com.luoyk.osf.core.definition.AbstractOsf;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioOsfAutoConfigure extends AbstractOsfAutoConfigure {
    @Override
    public AbstractOsf abstractOsfProvider() {
        return new MinioOsf();
    }
}
