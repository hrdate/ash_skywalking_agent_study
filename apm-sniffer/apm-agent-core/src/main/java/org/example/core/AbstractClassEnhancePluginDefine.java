package org.example.core;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
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
@Slf4j
public abstract class AbstractClassEnhancePluginDefine {

    /**
     * 为匹配到的字节码(类)新增的成员变量的名称
     */
    public static final String CONTEXT_ATTR_NAME = "_$EnhancedClassField_ws";

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

    /**
     * 增强类的主入口，绑定 当前类中哪些方法被拦截+具体的拦截器逻辑 到 builder(后续链式调用会把所有插件的配置都绑定上)
     */
    public DynamicType.Builder<?> define(TypeDescription typeDescription,
                                         DynamicType.Builder<?> builder,
                                         ClassLoader classLoader,
                                         EnhanceContext enhanceContext) {
        log.info("类: {}, 被插件: {} 增强 -- start", typeDescription, this.getClass().getName());
        DynamicType.Builder<?> newBuilder = this.enhance(typeDescription, builder, classLoader, enhanceContext);
        // 表示 当前类已经被增强过了
        enhanceContext.initializationStageCompleted();
        log.info("类: {}, 被插件: {} 增强 -- end", typeDescription, this.getClass().getName());
        return newBuilder;
    }

    /**
     * 具体的增强逻辑
     */
    private DynamicType.Builder<?> enhance(TypeDescription typeDescription,
                                           DynamicType.Builder<?> newBuilder,
                                           ClassLoader classLoader,
                                           EnhanceContext enhanceContext) {
        // 1. 静态方法增强
        newBuilder = this.enhanceClass(typeDescription, newBuilder, classLoader);
        // 2. 实例方法增强(包括构造方法)
        newBuilder = this.enhanceInstance(typeDescription, newBuilder, classLoader, enhanceContext);
        return newBuilder;
    }

    /**
     * 实例方法增强(包括构造方法)
     */
    protected abstract DynamicType.Builder<?> enhanceInstance(TypeDescription typeDescription,
                                                              DynamicType.Builder<?> newBuilder,
                                                              ClassLoader classLoader,
                                                              EnhanceContext context);

    /**
     * 静态方法增强
     */
    protected abstract DynamicType.Builder<?> enhanceClass(TypeDescription typeDescription,
                                                           DynamicType.Builder<?> newBuilder,
                                                           ClassLoader classLoader);
}
