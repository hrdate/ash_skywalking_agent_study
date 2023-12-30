package org.example.core;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.example.core.interceptor.ConstructorMethodsInterceptorPoint;
import org.example.core.interceptor.InstanceMethodsInterceptorPoint;
import org.example.core.interceptor.StaticMethodsInterceptorPoint;
import org.example.core.match.ClassMatch;

/**
 * 所有插件的顶级父类
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/30 6:10 PM
 */
public abstract class AbstractClassEnhancePluginDefine {
    /**
     * 表示当前插件要拦截/增强的类范围 <br/>
     * 相当于对应 {@link AgentBuilder#type(ElementMatcher)} 的 参数 {@code ElementMatcher<? super TypeDescription>}
     */
    protected abstract ClassMatch enhanceClass();

    /**
     * 实例方法的拦截点 <br/>
     * ps: 类下面多个方法可能使用不同的拦截器
     */
    protected abstract InstanceMethodsInterceptorPoint[] getInstanceMethodsInterceptorPoints();

    /**
     * 构造方法的拦截点 <br/>
     */
    protected abstract ConstructorMethodsInterceptorPoint[] getConstructorMethodsInterceptorPoints();

    /**
     * 静态方法的拦截点 <br/>
     * ps: 类下面多个方法可能使用不同的拦截器
     */
    protected abstract StaticMethodsInterceptorPoint[] getStaticMethodsInterceptorPoints();
}
