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

    public final Logger logger = Logger.getLogger(this.getClass().getName());

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
            logger.info("RabbitSender " + delayMessage.toString());
            return true;
        } catch (JsonProcessingException e) {
            logger.severe("JsonProcessingException");
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
        String delayQueue = DELAY_QUEUE + "." + timeToLive + ".ttl";
        args.put("x-message-ttl", timeToLive);
        //定义消息成死信之后进入的交换机
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        channel.queueDeclare(delayQueue, true, false, false, args);
        channel.exchangeDeclare(DELAY_EXCHANGE, "topic", true, false, null);
        //延迟消息队列绑定延迟交换机
        channel.queueBind(delayQueue, DELAY_EXCHANGE, ROUTING_KEY);
        logger.info("Binding queue " + delayQueue + " to exchange " + DELAY_EXCHANGE);
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
        logger.info("Binding queue " + DLX_QUEUE + " to exchange " + DLX_EXCHANGE);
    }
}
