package com.luoyk.osf.core.loacl;

import com.luoyk.osf.core.mq.DelayMessage;
import com.luoyk.osf.core.mq.OsfReceiver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义的延迟消息
 */
public class LocalDelay {

    private final int TIME_TO_LIVE;

    private final OsfReceiver osfReceiver;

    private final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            0L, TimeUnit.SECONDS,
            new SynchronousQueue<>());

    public static LocalDelay CURRENT;

    public LocalDelay(int TIME_TO_LIVE, OsfReceiver osfReceiver) {
        this.TIME_TO_LIVE = TIME_TO_LIVE;
        this.osfReceiver = osfReceiver;
        CURRENT = this;
    }

    public void newDelayMessage(DelayMessage delayMessage) {
        EXECUTOR_SERVICE.execute(() -> {
            try {
                Thread.sleep(TIME_TO_LIVE);
                osfReceiver.receive(delayMessage);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
