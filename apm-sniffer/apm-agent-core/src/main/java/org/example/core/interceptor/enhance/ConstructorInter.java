package org.example.core.interceptor.enhance;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * 通用的构造方法拦截器
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/31 6:27 PM
 */
@Slf4j
public class ConstructorInter {
    /**
     * 拦截器在各个位置执行某些逻辑，封装在此成员变量中
     */
    private ConstructorInterceptor interceptor;

    public ConstructorInter(String methodsInterceptorName, ClassLoader classLoader) {
    }


    /**
     * 具体的拦截器逻辑， 整体预留的填充逻辑和AOP切面编程类似
     */
    @RuntimeType
    public void intercept(
            // 构造器方法执行完后的实例对象
            @This Object obj,
            // 方被拦截的方法的法参数
            @AllArguments Object[] allArgs) throws Throwable {
        // 构造方法执行完毕后， 执行增强逻辑
        try {
            // 构造方法拦截的目标类一定新增了成员变量, 然后实现了该字段的getter,setter接口
            EnhancedInstance enhancedInstance = (EnhancedInstance) obj;
            interceptor.onConstruct(enhancedInstance,allArgs);
        } catch (Throwable t) {
            log.error("Constructor Interceptor: {}, enhance constructor method failed,  e: ",
                    interceptor.getClass().getName(), t);
        }
    }
}
