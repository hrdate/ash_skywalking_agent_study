package org.example.plugins.springmvc.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.example.core.interceptor.enhance.EnhancedInstance;
import org.example.core.interceptor.enhance.InstanceMethodAroundInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * SpringMVC 方法拦截器
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/30 9:52 PM
 */
@Slf4j
public class SpringmvcInterceptor implements InstanceMethodAroundInterceptor {
    @Override
    public void beforeMethod(EnhancedInstance obj, Method method, Object[] allArgs, Class<?>[] parameterTypes) {
        log.info("springMvc methodName: {}, args: {}", method.getName(), Arrays.toString(allArgs));
    }

    @Override
    public Object afterMethod(EnhancedInstance obj, Method method, Object[] allArgs, Class<?>[] parameterTypes, Object returnValue) {
        log.info("springmvc result: {}", returnValue);
        return returnValue;
    }

    @Override
    public void handleEx(EnhancedInstance obj, Method method, Object[] allArgs, Class<?>[] parameterTypes, Throwable t) {
        log.error("springmvc error, e :", t);
    }
}
