package com.luoyk.osf.autoconfigure.annotation;

import com.luoyk.osf.autoconfigure.common.OsfConfigureRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用osf服务注解
 * 开始扫描osfConfigure
 *
 * @author luoyk
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(OsfConfigureRegister.class)
public @interface EnableOsfClient {
}
