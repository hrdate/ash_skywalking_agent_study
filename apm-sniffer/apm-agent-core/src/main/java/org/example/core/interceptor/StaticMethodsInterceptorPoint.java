package org.example.core.interceptor;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * 静态方法拦截点
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/30 6:26 PM
 */
public interface StaticMethodsInterceptorPoint {
    /**
     * 表示要拦截的方法范围
     * {@link DynamicType.Builder#method(ElementMatcher)} 的匹配器参数
     */
    ElementMatcher<MethodDescription> getMethodsMatcher();

    /**
     * 获取被增强方法对应的拦截器
     */
    String getMethodsInterceptor();
}
