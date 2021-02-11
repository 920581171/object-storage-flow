package com.luoyk.osf.autoconfigure.definition;

import com.luoyk.osf.core.cache.OsfCache;
import com.luoyk.osf.core.helper.OsfHelper;
import com.luoyk.osf.core.mq.OsfReceiver;
import com.luoyk.osf.core.mq.OsfSender;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;

public interface OsfAutoConfigure extends ApplicationContextAware, EnvironmentAware {

    /**
     * osf操作类bean
     */
    OsfHelper osfHelper();

    /**
     * 缓存控制bean
     */
    OsfCache osfCache();

    /**
     * 消息队列接收bean
     */
    OsfReceiver osfReceiver();

    /**
     * 消息队列发送bean
     */
    OsfSender osfSender();

}
