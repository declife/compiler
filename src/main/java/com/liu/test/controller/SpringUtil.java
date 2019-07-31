package com.liu.test.controller;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


@Component
public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    private static ClassLoader classLoader;

    public SpringUtil() {
    }

    public static Object getBean(String beanName) {
        return applicationContext != null ? applicationContext.getBean(beanName) : null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.applicationContext = applicationContext;
    }

    public static ClassLoader getTomcatClassLoader() {
        return classLoader;
    }

    public static void setTomcatClassLoader(ClassLoader tomcatClassLoader) {
        classLoader = tomcatClassLoader;
    }
}

