package com.luoyk.osf.core.mq.rabbit;

import com.luoyk.osf.core.mq.DelayMessage;
import com.luoyk.osf.core.mq.OsfReceiver;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitReceiver implements OsfReceiver {

    private final RabbitTemplate rabbitTemplate;

    public RabbitReceiver(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public boolean receive(DelayMessage delayMessage) {
        return false;
    }
}
