package com.luoyk.osf.core.mq;

/**
 * 定义消息队列接收操作
 */
public interface OsfReceiver {
    boolean receive(DelayMessage delayMessage);
}
