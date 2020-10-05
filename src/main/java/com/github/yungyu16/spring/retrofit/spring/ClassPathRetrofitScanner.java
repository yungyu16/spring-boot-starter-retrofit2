package com.github.yungyu16.spring.retrofit.spring;

import com.github.yungyu16.spring.retrofit.annotion.RetrofitClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Set;

/**
 * CreatedDate: 2020/9/25
 * Author: songjialin
 */
public class ClassPathRetrofitScanner extends ClassPathBeanDefinitionScanner {

    public ClassPathRetrofitScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
        addIncludeFilter(new AnnotationTypeFilter(RetrofitClient.class));
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        if (beanDefinitions.isEmpty()) {
            logger.warn("No Retrofit client was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
        } else {
            processBeanDefinitions(beanDefinitions);
        }
        return beanDefinitions;
    }

    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        GenericBeanDefinition definition;
        for (BeanDefinitionHolder holder : beanDefinitions) {
            definition = (GenericBeanDefinition) holder.getBeanDefinition();
            String retrofitClientClass = definition.getBeanClassName();
            logger.info("收集到RetrofitClient->beanName:" + holder.getBeanName() + " interface:" + retrofitClientClass + "");
            Assert.notNull(retrofitClientClass, "retrofitClientClass");
            definition.getConstructorArgumentValues().addGenericArgumentValue(retrofitClientClass); // issue #59
            definition.setBeanClass(RetrofitClientFactoryBean.class);
        }
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

    @Override
    protected boolean checkCandidate(@NotNull String beanName, @NotNull BeanDefinition beanDefinition) {
        if (super.checkCandidate(beanName, beanDefinition)) {
            return true;
        } else {
            logger.warn("Skipping Retrofit client with name '" + beanName
                    + "' and '" + beanDefinition.getBeanClassName() + "' Interface"
                    + ". Bean already defined with the same name!");
            return false;
        }
    }
}
