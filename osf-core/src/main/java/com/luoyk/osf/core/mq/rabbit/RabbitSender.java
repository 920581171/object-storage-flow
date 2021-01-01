package com.luoyk.osf.core.mq.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luoyk.osf.core.mq.DelayMessage;
import com.luoyk.osf.core.mq.OsfSender;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class RabbitSender implements OsfSender {

    public static final Logger LOGGER = Logger.getLogger(RabbitSender.class.getName());

    /**
     * 延迟队列
     */
    public static final String DELAY_QUEUE = "osf.delay.queue";

    /**
     * 延迟交换机
     */
    public static final String DELAY_EXCHANGE = "osf.delay.exchange";

    /**
     * 死信队列
     */
    public static final String DLX_QUEUE = "osf.dlx.queue";

    /**
     * 死信交换机
     */
    public static final String DLX_EXCHANGE = "osf.dlx.exchange";

    /**
     * 路由key
     */
    public static final String ROUTING_KEY = "osf.routing.key";

    private final int timeToLive;

    private final RabbitTemplate rabbitTemplate;

    public RabbitSender(int timeToLive, RabbitTemplate rabbitTemplate) {
        this.timeToLive = timeToLive;
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.execute(channel -> {
            delayDeclare(channel);
            dlxDeclare(channel);
            return channel;
        });
    }

    /**
     * 消息发送
     *
     * @param delayMessage 消息内容
     */
    @Override
    public boolean send(DelayMessage delayMessage) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final byte[] bytes = objectMapper.writeValueAsBytes(delayMessage);
            rabbitTemplate.convertAndSend(DELAY_EXCHANGE, ROUTING_KEY, bytes);
            return true;
        } catch (JsonProcessingException e) {
            LOGGER.severe("JsonProcessingException");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 构建延迟消息队列
     *
     * @param channel channel
     */
    public void delayDeclare(Channel channel) throws IOException {
        final Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", timeToLive);
        //定义消息成死信之后进入的交换机
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        channel.queueDeclare(DELAY_QUEUE, true, false, false, args);
        channel.exchangeDeclare(DELAY_EXCHANGE, "topic", true, false, null);
        //延迟消息队列绑定延迟交换机
        channel.queueBind(DELAY_QUEUE, DELAY_EXCHANGE, ROUTING_KEY);
    }

    /**
     * 构建死信队列
     *
     * @param channel channel
     */
    public void dlxDeclare(Channel channel) throws IOException {
        channel.queueDeclare(DLX_QUEUE, true, false, false, null);
        channel.exchangeDeclare(DLX_EXCHANGE, "topic", true, false, null);
        //死信队列绑定死信交换机
        channel.queueBind(DLX_QUEUE, DLX_EXCHANGE, ROUTING_KEY);
    }
}
