package com.luoyk.osf.core.loacl;

import com.luoyk.osf.core.mq.DelayMessage;
import com.luoyk.osf.core.mq.OsfSender;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.lang.NonNull;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * 本地消息发送者
 *
 * @author luoyk
 */
public class LocalSender implements OsfSender, ApplicationEventPublisherAware {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final int timeToLive;

    private final DelayQueue<LocalDelayed> queue = new DelayQueue<>();

    public LocalSender(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    @Override
    public boolean send(DelayMessage delayMessage) {
        logger.info("LocalSender " + delayMessage.toString());
        queue.put(new LocalDelayed(delayMessage, timeToLive));
        return true;
    }

    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    applicationEventPublisher.publishEvent(new LocalEvent(queue.take().getDelayMessage()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
