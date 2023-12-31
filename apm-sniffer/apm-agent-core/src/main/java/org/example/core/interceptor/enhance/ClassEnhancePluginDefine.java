package org.example.core.interceptor.enhance;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.matcher.ElementMatcher;
import org.example.core.AbstractClassEnhancePluginDefine;
import org.example.core.EnhanceContext;
import org.example.core.interceptor.ConstructorMethodsInterceptorPoint;
import org.example.core.interceptor.InstanceMethodsInterceptorPoint;
import org.example.core.interceptor.StaticMethodsInterceptorPoint;

import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * 所有插件直接or间接继承当前类, 当前类完成具体的增强逻辑(transform的方法拦截范围指定，以及拦截器指定)
 * <p>
 * 当前类的作用相当于:
 * {@code DynamicType.Builder<?> newBuilder = builder.method(xx).intercept(MethodDelegation.to(xx))}
 * </p>
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/31 5:57 PM
 */
@Slf4j
public abstract class ClassEnhancePluginDefine extends AbstractClassEnhancePluginDefine {
    @Override
    protected DynamicType.Builder<?> enhanceInstance(TypeDescription typeDescription, DynamicType.Builder<?> newBuilder, ClassLoader classLoader, EnhanceContext context) {
        ConstructorMethodsInterceptorPoint[] constructorPoints = getConstructorMethodsInterceptorPoints();
        InstanceMethodsInterceptorPoint[] instanceMethodPoints = getInstanceMethodsInterceptorPoints();
        // 构造方法拦截点是否存在
        boolean existedConstructorPoint = null != constructorPoints && constructorPoints.length > 0;
        // 实例方法拦截点是否存在
        boolean existedInstanceMethodPoint = null != instanceMethodPoints && instanceMethodPoints.length > 0;
        if (!existedConstructorPoint && !existedInstanceMethodPoint) {
            // 都不存在，则拦截的类范围内，没有需要拦截器增强的方法
            return newBuilder;
        }

        // 如果是头一次增强当前类, 则新增用于传递中间值的成员变量 (如果还没实现EnhanceContext接口，且没有新增过字段，则新增)
        if(!typeDescription.isAssignableFrom(EnhancedInstance.class)
            && !context.isObjectExtended()) {
            // 因为 Byte Buddy大多数类都是不可变的，所以需要变量接收
            newBuilder = newBuilder
                    // 新增成员变量，用于拦截器逻辑传递中间值
                    .defineField(CONTEXT_ATTR_NAME,Object.class, Opcodes.ACC_PRIVATE | Opcodes.ACC_VOLATILE)
                    // 实现 getter, setter接口
                    .implement(EnhancedInstance.class)
                    .intercept(FieldAccessor.ofField(CONTEXT_ATTR_NAME));
            // 当前类已经拓展过(新增过成员变量)
            context.objectExtendedCompleted();
        }

        // 1.构造方法增强
        if(existedConstructorPoint) {
            String typeName = typeDescription.getTypeName();
            for (ConstructorMethodsInterceptorPoint constructorPoint : constructorPoints) {
                String constructorInterceptorName = constructorPoint.getConstructorInterceptor();
                if (null == constructorInterceptorName || "".equals(constructorInterceptorName.trim())) {
                    log.error("插件: {} 没有指定目标类: {} 的构造方法对应的拦截器", this.getClass().getName(), typeName);
                }
                ElementMatcher<MethodDescription> constructorMatcher = constructorPoint.getConstructorMatcher();
                newBuilder = newBuilder.constructor(constructorMatcher)
                        .intercept(SuperMethodCall.INSTANCE.andThen(
                                // 构造方法直接结束后调用
                                MethodDelegation.withDefaultConfiguration()
                                        .to(new ConstructorInter(constructorInterceptorName, classLoader))
                        ));
            }
        }
        // 2. 实例方法增强
        if(existedInstanceMethodPoint) {
            String typeName = typeDescription.getTypeName();
            for (InstanceMethodsInterceptorPoint instancePoint : instanceMethodPoints) {
                String instanceInterceptorName = instancePoint.getMethodsInterceptor();
                if (null == instanceInterceptorName || "".equals(instanceInterceptorName.trim())) {
                    log.error("插件: {} 没有指定目标类: {} 的实例方法对应的拦截器", this.getClass().getName(), typeName);
                }
                ElementMatcher<MethodDescription> methodsMatcher = instancePoint.getMethodsMatcher();
                newBuilder = newBuilder.method(not(isStatic()).and(methodsMatcher))
                        .intercept(MethodDelegation.withDefaultConfiguration()
                                .to(new InstanceMethodsInter(instanceInterceptorName, classLoader)));
            }
        }
        return newBuilder;
    }

    @Override
    protected DynamicType.Builder<?> enhanceClass(TypeDescription typeDescription, DynamicType.Builder<?> newBuilder, ClassLoader classLoader) {
        StaticMethodsInterceptorPoint[] staticPoint = getStaticMethodsInterceptorPoints();
        if (null == staticPoint || staticPoint.length == 0) {
            // 当前插件没有指定 静态方法的增强逻辑(方法范围+拦截器), 则直接返回链式builder
            return newBuilder;
        }
        String typeName = typeDescription.getTypeName();
        for (StaticMethodsInterceptorPoint staticMethodsInterceptorPoint : staticPoint) {
            String methodsInterceptorName = staticMethodsInterceptorPoint.getMethodsInterceptor();
            if (null == methodsInterceptorName || "".equals(methodsInterceptorName.trim())) {
                log.error("插件: {} 没有指定目标类: {} 的静态方法对应的拦截器", this.getClass().getName(), typeName);
            }
            ElementMatcher<MethodDescription> methodsMatcher = staticMethodsInterceptorPoint.getMethodsMatcher();
            newBuilder = newBuilder.method(isStatic().and(methodsMatcher))
                    .intercept(MethodDelegation.withDefaultConfiguration()
                            .to(new StaticMethodsInter(methodsInterceptorName, classLoader)));
        }
        return newBuilder;
    }
}
