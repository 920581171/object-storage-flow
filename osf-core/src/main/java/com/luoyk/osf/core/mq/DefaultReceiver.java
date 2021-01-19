package com.luoyk.osf.core.mq;

import com.luoyk.osf.core.definition.AbstractOsf;

/**
 * 默认的receiver抽象类
 *
 * @author luoyk
 */
public abstract class DefaultReceiver implements OsfReceiver, MessageHandler {

    private final MessageHandler messageHandler;

    private final AbstractOsf abstractOsf;

    public DefaultReceiver(AbstractOsf abstractOsf) {
        this.messageHandler = abstractOsf instanceof MessageHandler ? ((MessageHandler) abstractOsf) : this;
        this.abstractOsf = abstractOsf;
    }

    @Override
    public final boolean receive(DelayMessage delayMessage) {
        return messageHandler.handler(delayMessage);
    }

    public AbstractOsf getAbstractOsf() {
        return abstractOsf;
    }
}
