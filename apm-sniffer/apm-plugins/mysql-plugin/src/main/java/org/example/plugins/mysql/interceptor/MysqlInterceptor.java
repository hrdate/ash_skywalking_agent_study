package org.example.plugins.mysql.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.example.core.interceptor.enhance.EnhancedInstance;
import org.example.core.interceptor.enhance.InstanceMethodAroundInterceptor;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * MySQL 拦截器
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/30 9:52 PM
 */
@Slf4j
public class MysqlInterceptor implements InstanceMethodAroundInterceptor {
    @Override
    public void beforeMethod(EnhancedInstance obj, Method method, Object[] allArgs, Class<?>[] parameterTypes) {
        // 模拟参数传递，比如获取到SQL语句，然后需要随后续sql直接结果传递给OAP
        obj.setSkyWalkingDynamicField("select * from user = ?");
        log.info("mysql methodName: {}, args: {}", method.getName(), Arrays.toString(allArgs));
    }

    @Override
    public Object afterMethod(EnhancedInstance obj, Method method, Object[] allArgs, Class<?>[] parameterTypes, Object returnValue) {
        log.info("Mysql result : {}" ,returnValue);
        Object sql = obj.getSkyWalkingDynamicField();
        log.info("MySQL afterMethod, sql: {}", sql);
        return returnValue;
    }

    @Override
    public void handleEx(EnhancedInstance obj, Method method, Object[] allArgs, Class<?>[] parameterTypes, Throwable t) {
        log.error("MySQL handleEx, e:" ,t);
    }
}
