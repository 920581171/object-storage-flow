package com.luoyk.osf.core.loacl;

import com.google.common.eventbus.Subscribe;
import com.luoyk.osf.core.definition.AbstractOsf;
import com.luoyk.osf.core.mq.DelayMessage;
import com.luoyk.osf.core.mq.OsfReceiver;

/**
 * 本地消息发送者
 *
 * @author luoyk
 */
public class LocalReceiver implements OsfReceiver {

    private final AbstractOsf abstractOsf;

    public LocalReceiver(AbstractOsf abstractOsf) {
        this.abstractOsf = abstractOsf;
        LocalEventBus.EVENT_BUS.register(this);
    }

    @Override
    @Subscribe
    public boolean receive(DelayMessage delayMessage) {
        switch (delayMessage.getFileType()) {
            case FILE:
                abstractOsf.getFileAction().deleteTemp(delayMessage.getTempId());
                break;
            case PICTURE:
                abstractOsf.getPictureAction().deleteTemp(delayMessage.getTempId());
                break;
        }
        return true;
    }
}
