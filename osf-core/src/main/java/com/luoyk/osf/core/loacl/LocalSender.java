package com.luoyk.osf.core.loacl;

import com.luoyk.osf.core.mq.DelayMessage;
import com.luoyk.osf.core.mq.OsfSender;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 本地消息发送者
 *
 * @author luoyk
 */
public class LocalSender implements OsfSender {

    private final int timeToLive;

    private final DelayQueue<LocalDelayed> queue = new DelayQueue<>();

    public LocalSender(int timeToLive) {
        this.timeToLive = timeToLive;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    LocalEventBus.EVENT_BUS.post(queue.take().getDelayMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean send(DelayMessage delayMessage) {
        queue.put(new LocalDelayed(delayMessage, timeToLive));
        return true;
    }
}
