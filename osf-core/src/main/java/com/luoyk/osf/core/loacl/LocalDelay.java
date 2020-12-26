package com.luoyk.osf.core.loacl;

import com.luoyk.osf.core.definition.AbstractOsf;
import com.luoyk.osf.core.mq.DelayMessage;
import com.luoyk.osf.core.mq.OsfReceiver;
import com.luoyk.osf.core.mq.OsfSender;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义的延迟消息
 *
 * @author luoyk
 */
public class LocalDelay implements OsfReceiver, OsfSender {

    private final int TIME_TO_LIVE;

    private final AbstractOsf abstractOsf;

    private final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            0L, TimeUnit.SECONDS,
            new SynchronousQueue<>());

    public LocalDelay(int TIME_TO_LIVE, AbstractOsf abstractOsf) {
        this.TIME_TO_LIVE = TIME_TO_LIVE;
        this.abstractOsf = abstractOsf;
    }

    @Override
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

    @Override
    public boolean send(DelayMessage delayMessage) {
        EXECUTOR_SERVICE.execute(() -> {
            try {
                Thread.sleep(TIME_TO_LIVE);
                receive(delayMessage);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        return true;
    }
}
