package com.luoyk.osf.core.loacl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.luoyk.osf.core.mq.DelayMessage;
import com.luoyk.osf.core.mq.OsfSender;

import java.time.Duration;
import java.util.BitSet;

/**
 * 本地消息发送者
 *
 * @author luoyk
 */
public class LocalSender implements OsfSender {

    private final int timeToLive;

    public LocalSender(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    @Override
    public boolean send(DelayMessage delayMessage) {
        LocalThreadPool.EXECUTOR_SERVICE.execute(() -> {
            try {
                Thread.sleep(timeToLive);
                LocalThreadPool.EVENT_BUS.post(delayMessage);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        return true;
    }
}
