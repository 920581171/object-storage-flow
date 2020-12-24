package com.luoyk.osf.core.mq;

/**
 * 定义消息队列发送操作
 */
public interface OsfSender {
    boolean send(DelayMessage delayMessage);
}
