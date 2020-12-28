package com.luoyk.osf.core.loacl;

import com.luoyk.osf.core.mq.DelayMessage;
import com.luoyk.osf.core.mq.OsfSender;

/**
 * 本地消息发送者
 *
 * @author luoyk
 */
public class LocalSender implements OsfSender {

    private final int timeToLive;

    private final LocalReceiver localReceiver;

    public LocalSender(int timeToLive, LocalReceiver localReceiver) {
        this.timeToLive = timeToLive;
        this.localReceiver = localReceiver;
    }

    @Override
    public boolean send(DelayMessage delayMessage) {
        LocalThreadPool.EXECUTOR_SERVICE.execute(() -> {
            try {
                Thread.sleep(timeToLive);
                localReceiver.receive(delayMessage);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        return true;
    }
}
