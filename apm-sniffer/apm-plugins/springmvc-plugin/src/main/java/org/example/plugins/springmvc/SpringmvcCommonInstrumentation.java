package org.example.plugins.springmvc;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.example.core.AbstractClassEnhancePluginDefine;
import org.example.core.interceptor.ConstructorMethodsInterceptorPoint;
import org.example.core.interceptor.InstanceMethodsInterceptorPoint;
import org.example.core.interceptor.StaticMethodsInterceptorPoint;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static net.bytebuddy.matcher.ElementMatchers.nameEndsWith;

/**
 * 定义Springmvc插件 公共抽象类
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/30 9:34 PM
 */
public abstract class SpringmvcCommonInstrumentation extends AbstractClassEnhancePluginDefine {
    @Override
    protected InstanceMethodsInterceptorPoint[] getInstanceMethodsInterceptorPoints() {
        // 之前单独实现的MySQL agent，我们只拦截实例方法，这里也只拦截实例方法
        return new InstanceMethodsInterceptorPoint[]{
                new InstanceMethodsInterceptorPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        // 拦截的方法范围
                        return not(isStatic())
                                .and(isAnnotatedWith(nameStartsWith("org.springframework.web.bind.annotation")
                                        .and((nameEndsWith("Mapping")))));
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return "org.example.plugins.springmvc.interceptor.SpringmvcInterceptor";
                    }
                }
        };
    }

    @Override
    protected ConstructorMethodsInterceptorPoint[] getConstructorMethodsInterceptorPoints() {
//        return new ConstructorMethodsInterceptorPoint[0];
        return null;
    }

    @Override
    protected StaticMethodsInterceptorPoint[] getStaticMethodsInterceptorPoints() {
        // 或者 return null, 反正 MySQL 插件，我们这里只拦截实例方法
        return new StaticMethodsInterceptorPoint[0];
    }
}
