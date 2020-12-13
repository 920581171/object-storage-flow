package com.luoyk.osf.autoconfigure.common;

import com.luoyk.osf.autoconfigure.definition.AbstractOsfAutoConfigure;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

/**
 * 手动注册Configure
 *
 * @author luoyk
 */
public class OsfConfigureRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private final Logger logger = Logger.getLogger(OsfConfigureRegister.class.getName());

    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {

        //默认的实现路径
        final String basePath = "com.luoyk.osf.achieve";

        //类路径扫描器
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false, environment);

        //定义扫描到的类的过滤条件
        scanner.addIncludeFilter(new AbstractTypeHierarchyTraversingFilter(true, false) {
            /**
             * 只返回继承了{@link AbstractOsfAutoConfigure}的类
             */
            @Override
            public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
                return Objects.equals(metadataReader.getClassMetadata().getSuperClassName(), AbstractOsfAutoConfigure.class.getName());
            }
        });

        //开始搜索默认路径下的所有类
        final Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePath);

        //如果没有找到实现类
        if (candidateComponents.size() == 0) {
            throw new RuntimeException("No found osf achieve on the classpath");
        }

        //如果找到的类数量大于1,抛出异常
        if (candidateComponents.size() > 1) {
            StringBuilder builder = new StringBuilder("found achieve \n");
            for (BeanDefinition candidateComponent : candidateComponents) {
                final String fileSystemName = Optional.ofNullable(candidateComponent.getBeanClassName())
                        .orElseThrow(() -> new RuntimeException("There can only be one osf achieve on the classpath"))
                        .replace(basePath, "")
                        .replaceFirst("\\.", "")
                        .split("\\.")[0];
                builder.append(fileSystemName).append("\n");
            }
            builder.append("There can only be one osf achieve on the classpath");
            throw new RuntimeException(builder.toString());
        }

        //注册configure
        beanDefinitionRegistry.registerBeanDefinition("osfConfigure", (BeanDefinition) candidateComponents.toArray()[0]);
        logger.info("Initialization osf configure from " + ((BeanDefinition) candidateComponents.toArray()[0]).getBeanClassName());
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
