package com.luoyk.osf.achieve.mongodb;

import com.luoyk.osf.autoconfigure.definition.AbstractOsfAutoConfigure;
import com.luoyk.osf.core.definition.Osf;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoDBOsfAutoConfigure extends AbstractOsfAutoConfigure {
    @Override
    public Osf abstractOsfProvider() {
        return new MongoDBOsf();
    }
}
