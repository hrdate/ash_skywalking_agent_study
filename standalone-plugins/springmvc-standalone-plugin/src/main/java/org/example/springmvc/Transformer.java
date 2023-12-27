package org.example.springmvc;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.utility.JavaModule;

import java.security.ProtectionDomain;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * SpringMVC 指定方法拦截 + 指定拦截器
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/27 3:10 PM
 */
@Slf4j
public class Transformer implements AgentBuilder.Transformer {
    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder,
                                            TypeDescription typeDescription,
                                            // 加载 typeDescription 的类加载器
                                            ClassLoader classLoader,
                                            JavaModule module,
                                            ProtectionDomain protectionDomain) {
        log.info("SpringMVC transform, target Type: {}", typeDescription.getActualName());
        DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> interceptor = builder
                .method(not(isStatic())
                        .and(isAnnotatedWith(nameStartsWith("org.springframework.web.bind.annotation")
                                .and((nameEndsWith("Mapping"))))))
                .intercept(MethodDelegation.to(new Interceptor()));
        return interceptor;
    }
}
