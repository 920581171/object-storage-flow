package com.luoyk.osf.core.mq.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luoyk.osf.core.definition.AbstractOsf;
import com.luoyk.osf.core.mq.DefaultReceiver;
import com.luoyk.osf.core.mq.DelayMessage;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;
import java.util.logging.Logger;

import static com.luoyk.osf.core.mq.rabbit.RabbitSender.DLX_QUEUE;

/**
 * 基于RabbitMQ的消息接收者
 *
 * @author luoyk
 */
public class RabbitReceiver extends DefaultReceiver {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final RabbitTemplate rabbitTemplate;

    public RabbitReceiver(RabbitTemplate rabbitTemplate, AbstractOsf abstractOsf) {
        super(abstractOsf);
        this.rabbitTemplate = rabbitTemplate;
        registerListener();
    }

    /**
     * 手动注册消息监听，以避免ack模式不一致情况
     */
    public void registerListener() {
        rabbitTemplate.execute(channel -> channel.basicConsume(DLX_QUEUE, false, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                ObjectMapper objectMapper = new ObjectMapper();
                final DelayMessage delayMessage = objectMapper.readValue(body, DelayMessage.class);
                logger.info("RabbitReceiver " + delayMessage.toString());
                receive(delayMessage);
                channel.basicAck(envelope.getDeliveryTag(), false);
                super.handleDelivery(consumerTag, envelope, properties, body);
            }
        }));
        logger.info("Rabbit queue listen: osf.dlx.queue");
    }

    @Override
    public boolean handler(DelayMessage delayMessage) {
        boolean deleted = false;
        switch (delayMessage.getFileType()) {
            case FILE:
                deleted = getAbstractOsf().getFileAction().deleteTemp(delayMessage.getTempPath());
                break;
            case PICTURE:
                deleted = getAbstractOsf().getPictureAction().deleteTemp(delayMessage.getTempPath());
                break;
        }

        if (deleted) {
            logger.info("Delete temp success:" + delayMessage.toString());
        } else {
            logger.severe("Delete temp fail:" + delayMessage.toString());
        }

        return deleted;
    }
}
