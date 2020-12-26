package com.luoyk.osf.core.mq;

/**
 * 定义消息队列发送操作
 *
 * @author luoyk
 */
public interface OsfSender {

    /**
     * 发送延迟消息
     *
     * @param delayMessage 消息内容
     * @return 发送成功或失败
     */
    boolean send(DelayMessage delayMessage);
}
