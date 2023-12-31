package org.example.plugins.mysql;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.example.core.AbstractClassEnhancePluginDefine;
import org.example.core.interceptor.ConstructorMethodsInterceptorPoint;
import org.example.core.interceptor.InstanceMethodsInterceptorPoint;
import org.example.core.interceptor.StaticMethodsInterceptorPoint;
import org.example.core.interceptor.enhance.ClassEnhancePluginDefine;
import org.example.core.match.ClassMatch;
import org.example.core.match.MultiClassNameMatch;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * 定义MySQL插件
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/30 9:34 PM
 */
public class MysqlInstrumentation extends ClassEnhancePluginDefine {
    @Override
    protected ClassMatch enhanceClass() {
        return MultiClassNameMatch.byMultiClassMatch(
                "com.mysql.cj.jdbc.ClientPreparedStatement",
                "com.mysql.cj.jdbc.ServerPreparedStatement");
    }

    @Override
    protected InstanceMethodsInterceptorPoint[] getInstanceMethodsInterceptorPoints() {
        // 之前单独实现的MySQL agent，我们只拦截实例方法，这里也只拦截实例方法
        return new InstanceMethodsInterceptorPoint[]{
                new InstanceMethodsInterceptorPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        // 拦截的方法范围
                        return named("execute").or(named("executeUpdate").or(named("executeQuery")));
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        // 这里拦截器使用类名，而不是要求传递Class类对象
                        return "org.example.plugins.mysql.interceptor.MysqlInterceptor";
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
