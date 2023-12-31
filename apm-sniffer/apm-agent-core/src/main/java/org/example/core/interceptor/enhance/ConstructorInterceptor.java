package org.example.core.interceptor.enhance;

import java.lang.reflect.Method;

/**
 * 构造方法 的拦截器 都需要实现当前接口
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/31 6:37 PM
 */
public interface ConstructorInterceptor {
    /**
     * 构造方法执行完后，后置调用
     */
    void onConstruct(EnhancedInstance obj, Object[] allArgs);
}
