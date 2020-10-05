package com.github.yungyu16.spring.retrofit.spring;

import com.github.yungyu16.spring.retrofit.annotion.EnableRetrofitClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * CreatedDate: 2020/9/25
 * Author: songjialin
 */
public class RetrofitBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, @NotNull BeanDefinitionRegistry registry) {
        AnnotationAttributes attrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableRetrofitClient.class.getName()));
        if (attrs == null) {
            throw new RuntimeException("这个不可能发生");
        }
        ClassPathRetrofitScanner scanner = new ClassPathRetrofitScanner(registry);
        List<String> basePackages = new ArrayList<String>();
        for (Class<?> clazz : attrs.getClassArray("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }
        basePackages.addAll(Arrays.asList(attrs.getStringArray("basePackages")));
        if (CollectionUtils.isEmpty(basePackages)) {
            throw new RuntimeException("Retrofit初始化失败，没有指定扫描的包路径；如无必要请不要调皮，去掉EnableRetrofitClient注解");
        }
        scanner.doScan(StringUtils.toStringArray(basePackages));
    }
}
