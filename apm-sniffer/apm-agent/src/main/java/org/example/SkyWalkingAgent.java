package org.example;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * 模仿SkyWalking 的 统一 Java Agent 入口类
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/30 3:08 PM
 */
public class SkyWalkingAgent {
    public static void premain(String args, Instrumentation instrumentation) {
        AgentBuilder agentBuilder = new AgentBuilder.Default()
                .type(
                        /*
                        // springmvc
                        isAnnotatedWith(named("org.springframework.stereotype.Controller")
                                .or(named("org.springframework.web.bind.annotation.RestController")))
                        // mysql
                        .or(named("com.mysql.cj.jdbc.ClientPreparedStatement")
                                .or(named("com.mysql.cj.jdbc.ServerPreparedStatement")))
                        // 其他插件 类拦截配置
                        */
                )
                .transform()
                .installOn(instrumentation);
    }

    private static class Transformer implements AgentBuilder.Transformer {

        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder,
                                                TypeDescription typeDescription,
                                                // 加载 typeDescription 的类加载器
                                                ClassLoader classLoader,
                                                JavaModule javaModule,
                                                ProtectionDomain protectionDomain) {
            return builder.method(
                    /*
                    // springmvc
                    not(isStatic())
                            .and(isAnnotatedWith(nameStartsWith("org.springframework.web.bind.annotation")
                                    .and((nameEndsWith("Mapping")))))
                    // mysql
                    .or(named("execute").or(named("executeUpdate").or(named("executeQuery"))))
                    */
            ).intercept(
                    // springMvc 需要使用 SpringMVC插件的Interceptor
                    // mysql 需要使用 mysql 插件的 Interceptor
                    MethodDelegation.to());
        }
    }
}
