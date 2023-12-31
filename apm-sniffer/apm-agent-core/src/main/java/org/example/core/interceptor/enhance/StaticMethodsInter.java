package org.example.core.interceptor.enhance;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * 通用的静态方法拦截器
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/31 6:27 PM
 */
@Slf4j
public class StaticMethodsInter {
    /**
     * 拦截器在各个位置执行某些逻辑，封装在此成员变量中
     */
    private StaticMethodAroundInterceptor interceptor;

    public StaticMethodsInter(String methodsInterceptorName, ClassLoader classLoader) {
    }


    /**
     * 具体的拦截器逻辑， 整体预留的填充逻辑和AOP切面编程类似
     */
    @RuntimeType
    public Object intercept(
            // 被拦截的目标类
            @Origin Class<?> clazz,
            // 被拦截的目标方法
            @Origin Method method,
            // 方被拦截的方法的法参数
            @AllArguments Object[] allArgs,
            // 调用原被拦截的目标方法
            @SuperCall Callable<?> zuper) throws Throwable {
        // 1. 前置增强
        try {
            interceptor.beforeMethod(clazz, method, allArgs, method.getParameterTypes());
        } catch (Throwable e) {
            log.error("Static Method Interceptor: {}, enhance method: {}, before method failed, e: ", interceptor.getClass().getName(), method.getName(), e);
        }
        Object returnValue = null;
        try {
            returnValue = zuper.call();
        } catch (Throwable e) {
            // 2. 异常处理
            try {
                interceptor.handleEx(clazz, method, allArgs, method.getParameterTypes(), e);
            } catch (Throwable innerError) {
                log.error("Static Method Interceptor: {}, enhance method: {}, handle Exception failed, e: ", interceptor.getClass().getName(), method.getName(), e);
            }
            // 继续上抛异常, 不影响原方法执行逻辑
            throw e;
        } finally {
            // 3. 后置增强
            try {
                returnValue = interceptor.afterMethod(clazz, method, allArgs, method.getParameterTypes(), returnValue);
            } catch (Throwable e) {
                log.error("Static Method Interceptor: {}, enhance method: {}, after method failed, e: ", interceptor.getClass().getName(), method.getName(), e);
            }
        }
        return returnValue;
    }
}
