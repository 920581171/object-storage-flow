package com.luoyk.osf.core.loacl;

import com.google.common.eventbus.Subscribe;
import com.luoyk.osf.core.definition.AbstractOsf;
import com.luoyk.osf.core.mq.DelayMessage;
import com.luoyk.osf.core.mq.OsfReceiver;

import java.util.logging.Logger;

/**
 * 本地消息发送者
 *
 * @author luoyk
 */
public class LocalReceiver implements OsfReceiver {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final AbstractOsf abstractOsf;

    public LocalReceiver(AbstractOsf abstractOsf) {
        this.abstractOsf = abstractOsf;
        LocalEventBus.EVENT_BUS.register(this);
    }

    @Override
    @Subscribe
    public boolean receive(DelayMessage delayMessage) {
        logger.info("LocalReceiver " + delayMessage.toString());
        boolean deleted = false;
        String tempPath = delayMessage.getTempPath();
        switch (delayMessage.getFileType()) {
            case FILE:
                deleted = abstractOsf.getFileAction().deleteTemp(tempPath);
                break;
            case PICTURE:
                deleted = abstractOsf.getPictureAction().deleteTemp(tempPath);
                break;
        }

        if (deleted) {
            logger.info("Delete temp success:" + tempPath);
        } else {
            logger.severe("Delete temp fail:" + tempPath);
        }

        return deleted;
    }
}
