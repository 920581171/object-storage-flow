package com.luoyk.osf.core.loacl;

import com.luoyk.osf.core.definition.achieve.AbstractOsf;
import com.luoyk.osf.core.mq.DefaultReceiver;
import com.luoyk.osf.core.mq.DelayMessage;
import org.springframework.context.ApplicationListener;

import java.util.logging.Logger;

/**
 * 本地消息发送者
 *
 * @author luoyk
 */
public class LocalReceiver extends DefaultReceiver implements ApplicationListener<LocalEvent> {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public LocalReceiver(AbstractOsf abstractOsf) {
        super(abstractOsf);
    }

    @Override
    public boolean handler(DelayMessage delayMessage) {
        logger.info("LocalReceiver " + delayMessage.toString());
        boolean deleted = false;
        String tempPath = delayMessage.getTempPath();
        switch (delayMessage.getFileType()) {
            case FILE:
                deleted = getAbstractOsf().getFileAction().deleteTemp(tempPath);
                break;
            case PICTURE:
                deleted = getAbstractOsf().getPictureAction().deleteTemp(tempPath);
                break;
        }
        return deleted;
    }

    @Override
    public void onApplicationEvent(LocalEvent event) {
        receive(((DelayMessage) event.getSource()));
    }
}
