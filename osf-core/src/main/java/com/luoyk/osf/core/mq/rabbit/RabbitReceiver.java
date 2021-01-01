package com.luoyk.osf.core.mq.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luoyk.osf.core.definition.AbstractOsf;
import com.luoyk.osf.core.mq.DelayMessage;
import com.luoyk.osf.core.mq.OsfReceiver;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;

import java.io.IOException;
import java.util.Map;

import static com.luoyk.osf.core.mq.rabbit.RabbitSender.DLX_QUEUE;

public class RabbitReceiver implements OsfReceiver {

    private final RabbitTemplate rabbitTemplate;

    private final AbstractOsf abstractOsf;

    public RabbitReceiver(RabbitTemplate rabbitTemplate, AbstractOsf abstractOsf) {
        this.rabbitTemplate = rabbitTemplate;
        this.abstractOsf = abstractOsf;
    }

    @RabbitListener(queues = DLX_QUEUE)
    public void listener(@Payload String msg, @Headers Map<String, Object> headers, Channel channel) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        final DelayMessage delayMessage = objectMapper.readValue(msg, DelayMessage.class);
        if (receive(delayMessage)) {
            Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
            channel.basicAck(deliveryTag, false);
        }
    }

    @Override
    public boolean receive(DelayMessage delayMessage) {
        switch (delayMessage.getFileType()) {
            case FILE:
                abstractOsf.getFileAction().deleteTemp(delayMessage.getTempId());
                break;
            case PICTURE:
                abstractOsf.getPictureAction().deleteTemp(delayMessage.getTempId());
                break;
        }
        return true;
    }
}
