package com.luoyk.osf.achieve.mongodb;

import com.luoyk.osf.autoconfigure.definition.AbstractOsfAutoConfigure;
import com.luoyk.osf.core.definition.AbstractOsf;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoDBOsfAutoConfigure extends AbstractOsfAutoConfigure {
    @Override
    public AbstractOsf abstractOsfProvider() {
        return new MongoDBOsf();
    }
}
