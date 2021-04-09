package com.luoyk.osf.autoconfigure.definition;

import com.luoyk.osf.core.cache.OsfCache;
import com.luoyk.osf.core.cache.RedisOsfCache;
import com.luoyk.osf.core.definition.Osf;
import com.luoyk.osf.core.definition.achieve.AbstractOsf;
import com.luoyk.osf.core.helper.OsfMultipleTemplate;
import com.luoyk.osf.core.helper.OsfTemplate;
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
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.lang.NonNull;

import java.util.logging.Logger;

/**
 * 抽象装配类，由各自实现继承
 * 继承后需添加{@link org.springframework.context.annotation.Configuration}注解
 *
 * @author luoyk
 */
public abstract class AbstractOsfAutoConfigure implements OsfAutoConfigure, BeanFactoryPostProcessor {

    private static int configSize = 0;

    protected final Logger logger = Logger.getLogger(AbstractOsfAutoConfigure.class.getName());

    private ApplicationContext applicationContext;

    private Environment environment;

    private int timeToLive = 2 * 60 * 60 * 1000;

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        final Osf osf = abstractOsfProvider();
        if (configSize == 0) {
            configurableListableBeanFactory.registerSingleton("osf", osf);
            configSize++;
            if (osf instanceof AbstractOsf) {
                logger.info("initialization OsfTemplate by " + osf.getClass());
                configurableListableBeanFactory.registerSingleton("osfHelper", new OsfTemplate((AbstractOsf) osf));
            }
            logger.info("initialization CustomHelper: " + osf.getClass().getSimpleName());
            // TODO: 2021/2/11 修改配置以支持自定义实现
            //  register new customOsf()

        } else {
            configurableListableBeanFactory.registerSingleton("osf" + configSize, abstractOsfProvider());
            final boolean osfTemplateExist = applicationContext.containsBean("osfHelper");
            final boolean osfMultipleTemplateExist = applicationContext.containsBean("osfMultipleTemplate");
            if (!osfTemplateExist) {
                logger.warning("no found 'osfTemplate', can not initialization osfMultipleTemplate");
                return;
            }

            if (!osfMultipleTemplateExist) {
                OsfMultipleTemplate osfMultipleTemplate = new OsfMultipleTemplate();
                OsfTemplate osfTemplate = (OsfTemplate) applicationContext.getBean("osfHelper");
                osfMultipleTemplate.addTemplate(osfTemplate);
                configurableListableBeanFactory.registerSingleton("osfMultipleTemplate", osfMultipleTemplate);
                logger.info("initialization osfMultipleTemplate");
            }

            if (osf instanceof AbstractOsf) {
                OsfMultipleTemplate osfMultipleTemplate = (OsfMultipleTemplate) applicationContext.getBean("osfMultipleTemplate");
                logger.info("OsfMultipleTemplate add OsfTemplate by " + osf.getClass());
                osfMultipleTemplate.addTemplate(new OsfTemplate((AbstractOsf) osf));
            }
        }
    }

    /**
     * 提供AbstractOsf
     *
     * @return AbstractOsf
     */
    public abstract Osf abstractOsfProvider();

    //todo cache/mq 在多写下的处理方案

    /**
     * 根据是否存在对应的缓存Bean，创建OsfCache Bean
     * 如果不存在，则使用默认的Cache实现
     */
    @Bean
    @SuppressWarnings("unchecked")
    public OsfCache osfCache() {
        if (applicationContext.containsBean("redisTemplate")) {
            logger.info("Found bean redisTemplate, initialization RedisOsfCache");
            final RedisTemplate<Object, Object> redisTemplate = (RedisTemplate<Object, Object>) applicationContext.getBean("redisTemplate");
            return new RedisOsfCache(timeToLive, redisTemplate);
        } else {
            logger.warning("No found cache, initialization LocalCache");
            return new LocalCache(timeToLive);
        }
    }

    /**
     * 根据是否存在对应的MQ Bean，创建OsfSender Bean
     * 如果不存在，则使用默认的OsfSender实现
     * bean的生成有顺序，先Sender创建队列，后生成Receiver监听队列
     */
    @Bean
    @Override
    public OsfSender osfSender() {
        if (applicationContext.containsBean("rabbitTemplate")) {
            final RabbitTemplate rabbitTemplate = (RabbitTemplate) applicationContext.getBean("rabbitTemplate");
            logger.info("Found bean rabbitTemplate, initialization RabbitSender");
            return new RabbitSender(timeToLive, rabbitTemplate);
        } else if (applicationContext.containsBean("jmsMessagingTemplate")) {
            final JmsMessagingTemplate jmsMessagingTemplate = (JmsMessagingTemplate) applicationContext.getBean("jmsMessagingTemplate");
            logger.info("Found bean jmsMessagingTemplate, initialization JmsMessagingSender");
            return new JmsMessagingSender(jmsMessagingTemplate);
        } else {
            logger.warning("No found mq, initialization LocalSender");
            return new LocalSender(timeToLive);
        }
    }

    /**
     * 根据是否存在对应的MQ Bean，创建OsfReceiver Bean
     * 如果不存在，则使用默认的OsfReceiver实现
     */
    @Bean
    @Override
    public OsfReceiver osfReceiver() {
        final Object osfHelper = applicationContext.getBean("osfHelper");

        if (osfHelper instanceof OsfTemplate) {
            final OsfTemplate osfTemplate = (OsfTemplate) osfHelper;
            final AbstractOsf abstractOsf = osfTemplate.getOsf();
            if (applicationContext.containsBean("rabbitTemplate")) {
                logger.info("Found bean rabbitTemplate, initialization RabbitReceiver");
                final RabbitTemplate rabbitTemplate = (RabbitTemplate) applicationContext.getBean("rabbitTemplate");
                return new RabbitReceiver(rabbitTemplate, abstractOsf);
            } else if (applicationContext.containsBean("jmsMessagingTemplate")) {
                logger.info("Found bean jmsMessagingTemplate, initialization JmsMessagingReceiver");
                final JmsMessagingTemplate jmsMessagingTemplate = (JmsMessagingTemplate) applicationContext.getBean("jmsMessagingTemplate");
                return new JmsMessagingReceiver(jmsMessagingTemplate, abstractOsf);
            } else {
                logger.warning("No found mq, initialization LocalReceiver");
                return new LocalReceiver(abstractOsf);
            }
        } else {
            // TODO: 2021/2/11 实现自定义配置
            logger.info("initialization CustomReceiver");
            return delayMessage -> false;
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

    public final Environment getEnvironment() {
        return environment;
    }

    @Override
    public final void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public final ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
