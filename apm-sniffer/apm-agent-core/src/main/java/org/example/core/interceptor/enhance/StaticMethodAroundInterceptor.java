package org.example.core.interceptor.enhance;

import java.lang.reflect.Method;

/**
 * 静态方法 的拦截器 都需要实现当前接口
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/31 6:37 PM
 */
public interface StaticMethodAroundInterceptor {
    /**
     * 前置增强逻辑
     */
    void beforeMethod(Class<?> clazz, Method method, Object[] allArgs, Class<?>[] parameterTypes);

    /**
     * finally 后置增强逻辑 (不管原方法是否异常都会执行)
     * @return 方法返回值
     */
    Object afterMethod(Class<?> clazz, Method method, Object[] allArgs, Class<?>[] parameterTypes, Object returnValue);

    /**
     * 异常处理
     */
    void handleEx(Class<?> clazz, Method method, Object[] allArgs, Class<?>[] parameterTypes, Throwable t);
}
