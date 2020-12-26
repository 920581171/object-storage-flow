package com.luoyk.osf.autoconfigure.definition;

import com.luoyk.osf.core.cache.OsfCache;
import com.luoyk.osf.core.cache.RedisOsfCache;
import com.luoyk.osf.core.definition.AbstractOsf;
import com.luoyk.osf.core.loacl.LocalCache;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;

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

        final String timeToLive = environment.getProperty("osf.timeToLive");
        final int ttl = timeToLive != null && !"".equals(timeToLive) ?
                Integer.parseInt(timeToLive) :
                2 * 60 * 60 * 1000;

        if (applicationContext.containsBean("redisTemplate")) {
            logger.info("Found bean redisTemplate ,initialization RedisOsfCache");
            final RedisTemplate<?, ?> redisTemplate = (RedisTemplate<?, ?>) applicationContext.getBean("redisTemplate");
            return new RedisOsfCache(ttl, redisTemplate);
        } else {
            logger.warning("No found bean redisTemplate ,initialization LocalCache");
            return new LocalCache(ttl);
        }
    }

    @Override
    public final void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public final void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
