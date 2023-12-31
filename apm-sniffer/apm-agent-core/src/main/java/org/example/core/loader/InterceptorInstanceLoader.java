package org.example.core.loader;

import org.example.core.interceptor.enhance.ConstructorInterceptor;
import org.example.core.interceptor.enhance.InstanceMethodAroundInterceptor;
import org.example.core.interceptor.enhance.StaticMethodAroundInterceptor;

/**
 * 插件内的 拦截器 的加载器
 * <p>
 * 拦截器包括:
 *     <ol>
 *       <li>构造方法拦截器: {@link ConstructorInterceptor}</li>
 *       <li>实例方法拦截器: {@link InstanceMethodAroundInterceptor}</li>
 *       <li>静态方法拦截器: {@link StaticMethodAroundInterceptor}</li>
 *     </ol>
 * </p>
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2024/1/1 4:05 AM
 */
public class InterceptorInstanceLoader {
    /**
     * @param interceptorClassName 插件中拦截器的全限制类名
     * @param targetClassLoader    要想在插件拦截器中能够访问到被拦截的类,
     *                             需要拦截器和被拦截的类使用同一个类加载器，或拦截器的类加载器是被拦截类的类加载器的子类
     * @return ConstructorInterceptor 或 InstanceMethodsAroundInterceptor 或 StaticMethodsAroundInterceptor 的实例
     */
    public static <T> T load(String interceptorClassName, ClassLoader targetClassLoader)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (targetClassLoader == null) {
            targetClassLoader = InterceptorInstanceLoader.class.getClassLoader();
        }
        // 新建类加载器，设置父类为 被拦截类的类加载器，保证 拦截器后续能访问到被拦截类
        AgentClassLoader classLoader = new AgentClassLoader(targetClassLoader);
        return (T) Class.forName(interceptorClassName, true, classLoader).newInstance();
    }
}
