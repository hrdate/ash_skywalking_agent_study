package org.example.springmvc;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * MySQL 方法被拦截后, 具体的拦截器逻辑(修改/增强)
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/27 3:10 PM
 */
@Slf4j
public class Interceptor {

    @RuntimeType
    public Object intercept(
            @This Object targetObj,
            @Origin Method targetMethod,
            @AllArguments Object[] targetMethodArgs,
            @SuperCall Callable<?> zuper) {
        log.info("before SpringMVC method: {}, args: {}", targetMethod.getName(), Arrays.toString(targetMethodArgs));
        Object returnValue = null;
        try {
            returnValue = zuper.call();
        } catch (Exception e) {
            log.warn("SpringMVC intercept error, e: ", e);
        } finally {
            log.info("after SpringMVC");
        }
        return returnValue;
    }
}
