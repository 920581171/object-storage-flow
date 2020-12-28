package com.luoyk.osf.core.mq.rabbit;

import com.luoyk.osf.core.mq.DelayMessage;
import com.luoyk.osf.core.mq.OsfSender;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitSender implements OsfSender {

    private final RabbitTemplate rabbitTemplate;

    public RabbitSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public boolean send(DelayMessage delayMessage) {
        return false;
    }
}
