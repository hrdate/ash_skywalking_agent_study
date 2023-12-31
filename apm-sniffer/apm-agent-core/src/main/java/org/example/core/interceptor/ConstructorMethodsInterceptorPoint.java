package org.example.core.interceptor;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * 构造方法拦截点
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/30 6:26 PM
 */
public interface ConstructorMethodsInterceptorPoint {
    /**
     * 表示要拦截的方法范围
     * {@link DynamicType.Builder#method(ElementMatcher)} 的匹配器参数
     */
    ElementMatcher<MethodDescription> getConstructorMatcher();

    /**
     * 获取被增强方法对应的拦截器, 必须是 {@link org.example.core.interceptor.enhance.ConstructorInterceptor}  实现类
     */
    String getConstructorInterceptor();
}
