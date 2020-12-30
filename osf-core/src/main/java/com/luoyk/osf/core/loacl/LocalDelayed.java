package com.luoyk.osf.core.loacl;

import com.luoyk.osf.core.mq.DelayMessage;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class LocalDelayed implements Delayed {

    private final DelayMessage delayMessage;

    private final long delay;

    public LocalDelayed(DelayMessage delayMessage, long delay) {
        this.delayMessage = delayMessage;
        this.delay = System.currentTimeMillis() + delay;
    }

    public DelayMessage getDelayMessage() {
        return delayMessage;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return delay - System.currentTimeMillis();
    }

    @Override
    public int compareTo(Delayed o) {
        return (int)(delay - ((LocalDelayed)o).delay);
    }
}
