package com.openquartz.easytransaction.starter.aop;

import com.openquartz.easytransaction.core.annotation.Tcc;
import java.lang.reflect.Method;
import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.StaticMethodMatcher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.lang.NonNull;

/**
 * AsyncFileExecutorAdvice
 *
 * @author svnee
 */
public class TccAnnotationAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

    private final transient Advice advice;
    private final transient Pointcut pointcut;

    public TccAnnotationAdvisor(TccTryMethodInterceptor interceptor) {
        this.advice = interceptor;
        this.pointcut = buildPointcut();
    }

    private Pointcut buildPointcut() {

        AnnotatedTccMethodPoint methodPoint = new AnnotatedTccMethodPoint();
        return new ComposablePointcut(methodPoint);
    }

    private static class AnnotatedTccMethodPoint implements Pointcut {

        public AnnotatedTccMethodPoint() {
        }

        @Override
        @NonNull
        public ClassFilter getClassFilter() {
            return ClassFilter.TRUE;
        }

        @Override
        @NonNull
        public MethodMatcher getMethodMatcher() {
            return new FullQualifiedNameMethodMatcher();
        }

        private static class FullQualifiedNameMethodMatcher extends StaticMethodMatcher {

            public FullQualifiedNameMethodMatcher() {
            }

            @Override
            public boolean matches(@NonNull Method method, @NonNull Class<?> targetClass) {
                return matchesMethod(method);
            }

            private boolean matchesMethod(Method method) {
                return method.getDeclaredAnnotation(Tcc.class) != null;
            }
        }
    }

    @Override
    @NonNull
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    @NonNull
    public Advice getAdvice() {
        return advice;
    }

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        if (this.advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
        }
    }
}
