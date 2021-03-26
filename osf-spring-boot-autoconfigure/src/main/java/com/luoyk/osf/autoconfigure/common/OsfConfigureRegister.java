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
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 手动注册Configure
 *
 * @author luoyk
 */
public class OsfConfigureRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    public static final String CUSTOM = "custom";

    //默认的实现路径
    public static final String basePath = "com.luoyk.osf.achieve";

    private final Logger logger = Logger.getLogger(OsfConfigureRegister.class.getName());

    private Environment environment;

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {

        //类路径扫描器
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false, environment);

        //定义扫描到的类的过滤条件
        scanner.addIncludeFilter(new AbstractTypeHierarchyTraversingFilter(true, false) {
            /**
             * 只返回继承了{@link AbstractOsfAutoConfigure}的类
             */
            @Override
            public boolean match(@NonNull MetadataReader metadataReader, @NonNull MetadataReaderFactory metadataReaderFactory) {
                return Objects.equals(metadataReader.getClassMetadata().getSuperClassName(), AbstractOsfAutoConfigure.class.getName());
            }
        });

        final String osfServer = environment.getProperty("osf.server");
        final String[] multipleWriter = environment.getProperty("osf.multipleWriter", String[].class);
        if (multipleWriter == null) {
            BeanDefinition beanDefinition = CUSTOM.equals(osfServer) ?
                    scannerCustom(annotationMetadata, scanner) :
                    scannerCommon(scanner, osfServer);

            //注册单个configure
            beanDefinitionRegistry.registerBeanDefinition("osfConfigure", beanDefinition);
            logger.info("Initialization osf configure from " + beanDefinition.getBeanClassName());
        } else {
            Map<String, BeanDefinition> beanDefinitionMap = scannerMultiple(annotationMetadata, scanner);

            Stream.concat(Stream.of(osfServer), Arrays.stream(multipleWriter))
                    .distinct()
                    .forEach(server -> {
                        BeanDefinition beanDefinition = Optional.ofNullable(beanDefinitionMap.get(server))
                                .orElseThrow(() -> new RuntimeException("osf.server must one of " +
                                        Arrays.toString(beanDefinitionMap.keySet().toArray()) +
                                        " or 'custom'"));

                        if (server.equals(osfServer)) {
                            beanDefinitionRegistry.registerBeanDefinition("osfConfigure", beanDefinition);
                            logger.info("Initialization main multiple osf configure from " + beanDefinition.getBeanClassName());
                        } else {
                            final String className = Optional.ofNullable(beanDefinition.getBeanClassName())
                                    .map(beanClassName -> beanClassName.split("\\."))
                                    .map(split -> split[split.length - 1])
                                    .map(simpleClassName -> simpleClassName.substring(0, 1).toLowerCase() + simpleClassName.substring(1))
                                    .orElseThrow(() -> new RuntimeException("no found className"));

                            beanDefinitionRegistry.registerBeanDefinition(className, beanDefinition);
                            logger.info("Initialization multiple osf configure from " + beanDefinition.getBeanClassName());
                        }
                    });
        }
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    /**
     * 扫描默认实现
     */
    public BeanDefinition scannerCommon(ClassPathScanningCandidateComponentProvider scanner, String osfServer) {

        //开始搜索默认路径下的所有类
        final Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePath);

        //如果没有找到实现类
        if (candidateComponents.size() == 0) {
            throw new RuntimeException("No found osf achieve on the classpath");
        }

        final Map<String, BeanDefinition> beanDefinitionMap = candidateComponents.stream()
                .collect(Collectors.toMap(bd -> Optional.ofNullable(bd.getBeanClassName())
                        .orElseThrow(() -> new RuntimeException("There can only be one osf achieve on the classpath"))
                        .replace(basePath, "")
                        .replaceFirst("\\.", "")
                        .split("\\.")[0], Function.identity()));

        final Set<String> serverKeySet = beanDefinitionMap.keySet();
        logger.info("Found osf achieve " + Arrays.toString(serverKeySet.toArray()) + " on the classpath");

        return Optional.ofNullable(beanDefinitionMap.get(osfServer))
                .orElseThrow(() -> new RuntimeException("No found osf achieve '" +
                        osfServer + "' ," +
                        "osf.server must one of " + Arrays.toString(serverKeySet.toArray()) +
                        " or 'custom'"
                ));
    }

    /**
     * 扫描自定义实现
     */
    public BeanDefinition scannerCustom(AnnotationMetadata annotationMetadata, ClassPathScanningCandidateComponentProvider scanner) {
        String className = annotationMetadata.getClassName();
        String basePath = className.substring(0, className.lastIndexOf("."));
        final Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePath);
        if (candidateComponents.size() == 0) {
            throw new RuntimeException("No found custom osf achieve");
        }

        if (candidateComponents.size() > 1) {
            throw new RuntimeException("There can only be one custom osf achieve");
        }

        return (BeanDefinition) candidateComponents.toArray()[0];
    }

    /**
     * 扫描多写实现
     */
    public Map<String, BeanDefinition> scannerMultiple(AnnotationMetadata annotationMetadata, ClassPathScanningCandidateComponentProvider scanner) {
        //搜索默认路径下的所有类
        final Set<BeanDefinition> commonCandidateComponents = scanner.findCandidateComponents(basePath);

        //搜索自定义路径下的类
        String className = annotationMetadata.getClassName();
        String customPath = className.substring(0, className.lastIndexOf("."));
        final Set<BeanDefinition> customCandidateComponents = scanner.findCandidateComponents(customPath);

        return Stream.concat(commonCandidateComponents.stream(), customCandidateComponents.stream())
                .collect(Collectors.toMap(bd -> Optional.ofNullable(bd.getBeanClassName())
                        .orElseThrow(() -> new RuntimeException("There can only be one osf achieve on the classpath"))
                        .replace(basePath, "")
                        .replaceFirst("\\.", "")
                        .split("\\.")[0], Function.identity()));
    }
}
