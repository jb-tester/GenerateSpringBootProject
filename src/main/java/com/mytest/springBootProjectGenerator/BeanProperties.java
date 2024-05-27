package com.mytest.springBootProjectGenerator;


public class BeanProperties {
    String packageName;
    String className;
    String beanName;
    String entityName;

    public BeanProperties(String packageName, String className, String beanName, String entityName) {
        this.packageName = packageName;
        this.className = className;
        this.beanName = beanName;
        this.entityName = entityName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getBeanName() {
        return beanName;
    }

    public String getEntityName() {
        return entityName;
    }
}
