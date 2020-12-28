package com.luoyk.osf.autoconfigure.definition;

import com.luoyk.osf.core.cache.OsfCache;
import com.luoyk.osf.core.cache.RedisOsfCache;
import com.luoyk.osf.core.definition.AbstractOsf;
import com.luoyk.osf.core.loacl.LocalCache;
import com.luoyk.osf.core.loacl.LocalReceiver;
import com.luoyk.osf.core.loacl.LocalSender;
import com.luoyk.osf.core.mq.OsfReceiver;
import com.luoyk.osf.core.mq.OsfSender;
import com.luoyk.osf.core.mq.jms.JmsMessagingReceiver;
import com.luoyk.osf.core.mq.jms.JmsMessagingSender;
import com.luoyk.osf.core.mq.rabbit.RabbitReceiver;
import com.luoyk.osf.core.mq.rabbit.RabbitSender;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsMessagingTemplate;

import java.util.logging.Logger;

/**
 * 抽象装配类，由各自实现继承
 * 继承后需添加{@link org.springframework.context.annotation.Configuration}注解
 *
 * @author luoyk
 */
public abstract class AbstractOsfAutoConfigure implements ApplicationContextAware, EnvironmentAware {

    protected final Logger logger = Logger.getLogger(this.getClass().getName());

    private ApplicationContext applicationContext;

    private Environment environment;

    private int timeToLive = 2 * 60 * 60 * 1000;

    /**
     * 提供AbstractOsf
     *
     * @return AbstractOsf
     */
    public abstract AbstractOsf abstractOsfProvider();

    /**
     * 创建AbstractOsf Bean
     */
    @Bean
    public AbstractOsf abstractOsf() {
        return abstractOsfProvider();
    }

    /**
     * 根据是否存在对应的缓存Bean，创建OsfCache Bean
     * 如果不存在，则使用默认的Cache实现
     */
    @Bean
    public OsfCache osfCache() {

        if (applicationContext.containsBean("redisTemplate")) {
            logger.info("Found bean redisTemplate, initialization RedisOsfCache");
            final RedisTemplate<?, ?> redisTemplate = (RedisTemplate<?, ?>) applicationContext.getBean("redisTemplate");
            return new RedisOsfCache(timeToLive, redisTemplate);
        } else {
            logger.warning("No found cache, initialization LocalCache");
            return new LocalCache(timeToLive);
        }
    }

    /**
     * 根据是否存在对应的MQ Bean，创建OsfReceiver Bean
     * 如果不存在，则使用默认的OsfReceiver实现
     */
    @Bean
    public OsfReceiver osfReceiver() {
        if (applicationContext.containsBean("rabbitTemplate")) {
            logger.info("Found bean rabbitTemplate, initialization RabbitReceiver");
            final RabbitTemplate rabbitTemplate = (RabbitTemplate) applicationContext.getBean("rabbitTemplate");
            return new RabbitReceiver(rabbitTemplate);
        } else if (applicationContext.containsBean("jmsMessagingTemplate")) {
            logger.info("Found bean jmsMessagingTemplate, initialization JmsMessagingReceiver");
            final JmsMessagingTemplate jmsMessagingTemplate = (JmsMessagingTemplate) applicationContext.getBean("jmsMessagingTemplate");
            return new JmsMessagingReceiver(jmsMessagingTemplate);
        } else {
            logger.warning("No found mq, initialization OsfReceiver");
            final AbstractOsf abstractOsf = (AbstractOsf) applicationContext.getBean("abstractOsf");
            return new LocalReceiver(abstractOsf);
        }
    }

    /**
     * 根据是否存在对应的MQ Bean，创建OsfSender Bean
     * 如果不存在，则使用默认的OsfSender实现
     */
    @Bean
    public OsfSender osfSender() {
        if (applicationContext.containsBean("rabbitTemplate")) {
            final RabbitTemplate rabbitTemplate = (RabbitTemplate) applicationContext.getBean("rabbitTemplate");
            logger.info("Found bean rabbitTemplate, initialization RabbitSender");
            return new RabbitSender(rabbitTemplate);
        } else if (applicationContext.containsBean("jmsMessagingTemplate")) {
            final JmsMessagingTemplate jmsMessagingTemplate = (JmsMessagingTemplate) applicationContext.getBean("jmsMessagingTemplate");
            logger.info("Found bean jmsMessagingTemplate, initialization JmsMessagingSender");
            return new JmsMessagingSender(jmsMessagingTemplate);
        } else {
            logger.warning("No found mq, initialization OsfSender");
            final OsfReceiver osfReceiver = (OsfReceiver) applicationContext.getBean("osfReceiver");
            if (!(osfReceiver instanceof LocalReceiver)) {
                throw new RuntimeException("No found bean localReceiver");
            }
            return new LocalSender(timeToLive, (LocalReceiver) osfReceiver);
        }
    }

    @Override
    public final void setEnvironment(Environment environment) {
        final String timeToLive = environment.getProperty("osf.timeToLive");
        if (timeToLive != null && !"".equals(timeToLive)) {
            this.timeToLive = Integer.parseInt(timeToLive);
        }

        this.environment = environment;
    }

    @Override
    public final void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
