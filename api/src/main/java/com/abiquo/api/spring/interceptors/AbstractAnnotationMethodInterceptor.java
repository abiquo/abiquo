package com.abiquo.api.spring.interceptors;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.ProxyConfig;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.util.ClassUtils;

/**
 * @author vmahe
 */
public abstract class AbstractAnnotationMethodInterceptor extends ProxyConfig implements
    BeanPostProcessor, BeanClassLoaderAware, Ordered
{
    private static final long serialVersionUID = 4464939218369356818L;

    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    private AnnotationMethodAdvisor advisor;

    private boolean append;

    protected AbstractAnnotationMethodInterceptor(
        final Class< ? extends Annotation> classAnnotationType,
        final Class< ? extends Annotation> methodAnnotationType)
    {
        super();
        this.append = true;
        this.advisor = new AnnotationMethodAdvisor(classAnnotationType, methodAnnotationType);
    }

    protected AbstractAnnotationMethodInterceptor(
        final Class< ? extends Annotation> classAnnotationType,
        final Class< ? extends Annotation> methodAnnotationType, final boolean append)
    {
        super();
        this.append = append;
        this.advisor = new AnnotationMethodAdvisor(classAnnotationType, methodAnnotationType);
    }

    @Override
    public void setBeanClassLoader(final ClassLoader classLoader)
    {
        this.beanClassLoader = classLoader;
    }

    @Override
    public final int getOrder()
    {
        // By default, this should run after all other post-processors, so that it can just add
        // an advisor to existing proxies rather than double-proxy.
        return LOWEST_PRECEDENCE;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName)
        throws BeansException
    {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName)
        throws BeansException
    {
        if (bean instanceof AopInfrastructureBean)
        {
            // Ignore AOP infrastructure such as scoped proxies.
            return bean;
        }

        Class< ? > targetClass = AopUtils.getTargetClass(bean);
        if (AopUtils.canApply(advisor, targetClass))
        {
            if (bean instanceof Advised)
            {
                if (append)
                {
                    ((Advised) bean).addAdvisor(advisor);
                }
                else
                {
                    ((Advised) bean).addAdvisor(0, advisor);
                }

                return bean;
            }
            else
            {
                ProxyFactory proxyFactory = new ProxyFactory(bean);
                // Copy our properties (proxyTargetClass etc) inherited from ProxyConfig.
                proxyFactory.copyFrom(this);
                proxyFactory.addAdvisor(advisor);
                return proxyFactory.getProxy(this.beanClassLoader);
            }
        }
        else
        {
            return bean;
        }
    }

    private class AnnotationMethodAdvisor extends AbstractPointcutAdvisor implements
        MethodInterceptor
    {
        private static final long serialVersionUID = 1L;

        private AnnotationMatchingPointcut pointcut;

        public AnnotationMethodAdvisor(final Class< ? extends Annotation> classAnnotationType,
            final Class< ? extends Annotation> methodAnnotationType)
        {
            super();
            this.pointcut =
                new AnnotationMatchingPointcut(classAnnotationType, methodAnnotationType);
        }

        @Override
        public Pointcut getPointcut()
        {
            return pointcut;
        }

        @Override
        public Advice getAdvice()
        {
            return this;
        }

        @Override
        public Object invoke(final MethodInvocation invocation) throws Throwable
        {
            Object ret = null;

            preIntercept(invocation);

            try
            {
                ret = invocation.proceed();
            }
            catch (InvocationTargetException ex)
            {
                throw ex.getTargetException();
            }

            postIntercept(invocation);

            return ret;
        }
    }

    /**
     * Executes pre-interception logic.
     * 
     * @param invocation The invocation object.
     * @throws Exception If something fails. Method invocation will be aborted.
     */
    protected void preIntercept(final MethodInvocation invocation) throws Exception
    {

    }

    /**
     * Executes post-interception logic.
     * 
     * @param invocation The invocation object.
     * @throws Exception If something fails.
     */
    protected void postIntercept(final MethodInvocation invocation) throws Exception
    {

    }

}
