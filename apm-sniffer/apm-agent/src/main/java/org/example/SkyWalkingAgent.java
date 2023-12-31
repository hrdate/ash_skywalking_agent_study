package org.example;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.utility.JavaModule;
import org.example.core.AbstractClassEnhancePluginDefine;
import org.example.core.EnhanceContext;
import org.example.core.PluginBoostrap;
import org.example.core.PluginFinder;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.nameEndsWith;

/**
 * 模仿SkyWalking 的 统一 Java Agent 入口类
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/30 3:08 PM
 */
@Slf4j
public class SkyWalkingAgent {
    public static void premain(String args, Instrumentation instrumentation) {
        log.info("进入premain");
        PluginFinder pluginFinder = null;
        try {
            pluginFinder = new PluginFinder(new PluginBoostrap().loadPlugins());
        } catch (Exception e) {
            log.error("Agent初始化失败, e: ", e);
            return;
        }

        ByteBuddy byteBuddy = new ByteBuddy().with(TypeValidation.of(true));
        AgentBuilder agentBuilder = new AgentBuilder.Default(byteBuddy);
                /*
                        // springmvc
                        isAnnotatedWith(named("org.springframework.stereotype.Controller")
                                .or(named("org.springframework.web.bind.annotation.RestController")))
                        // mysql
                        .or(named("com.mysql.cj.jdbc.ClientPreparedStatement")
                                .or(named("com.mysql.cj.jdbc.ServerPreparedStatement")))
                        // 其他插件 类拦截配置
                        */
        agentBuilder
                .ignore(nameEndsWith("BasicErrorController"))
                // 1. 因为 pluginFinder负责加载插件，所以维护了所有插件需要拦截的类范围
                .type(pluginFinder.buildMatch())
                // 2. 从 所有插件中提取需要拦截的方法和拦截器
                .transform(new Transformer(pluginFinder))
                .installOn(instrumentation);
    }

    private static class Transformer implements AgentBuilder.Transformer {
        /**
         * 这里Transformer 需要知道 类对应的方法拦截范围，以及拦截器，而pluginFinder在加载插件时正好汇总了这些信息
         */
        private PluginFinder pluginFinder;

        public Transformer(PluginFinder pluginFinder) {
            this.pluginFinder = pluginFinder;
        }

        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder,
                                                TypeDescription typeDescription,
                                                // 加载 typeDescription 的类加载器
                                                ClassLoader classLoader,
                                                JavaModule javaModule,
                                                ProtectionDomain protectionDomain) {
            log.info("typeDescription: {}, prepare to transform.", typeDescription.getActualName());
            // 1. 因为 PluginFinder 存储了 类与拦截方法范围, 拦截器之间的关系，所以从pluginFinder找每个插件拦截当前类时需要的方法范围和拦截器
            List<AbstractClassEnhancePluginDefine> pluginDefineList = pluginFinder.find(typeDescription);
            if (pluginDefineList.isEmpty()) {
                // 当前拦截的类 实际没有对应的 插件，则直接返回builder (下次进入当前方法就是下一个待判断的类了)
                log.info("pluginFinder拦截了指定类,但是没有对应的插件(正常不该出现该情况), typeDescription: {}", typeDescription.getActualName());
                return builder;
            }
            DynamicType.Builder<?> newBuilder = builder;
            EnhanceContext enhanceContext = new EnhanceContext();
            for (AbstractClassEnhancePluginDefine pluginDefine : pluginDefineList) {
                // 2. 每个插件都有指定的拦截方法范围和拦截器逻辑，遍历(构造)每个插件需要的拦截方法范围和拦截器逻辑
                DynamicType.Builder<?> possibleBuilder = pluginDefine.define(typeDescription, newBuilder, classLoader, enhanceContext);
                // 这里相当于平时Byte Buddy代码指定了 A方法拦截范围+a拦截器，然后后面继续链式调用 B方法拦截范围+b拦截器
                newBuilder =  possibleBuilder == null ? newBuilder : possibleBuilder;
            }
            if(enhanceContext.isEnhanced()) {
                log.info("class: {} has been enhanced", typeDescription.getActualName());
            }
            // 链式调用后，已经绑定了拦截typeDescription的插件集合的各自方法拦截范围+拦截器
            return newBuilder;
//            DynamicType.Builder<?> newBuilder = builder.method(
//                    /*
//                    // springmvc
//                    not(isStatic())
//                            .and(isAnnotatedWith(nameStartsWith("org.springframework.web.bind.annotation")
//                                    .and((nameEndsWith("Mapping")))))
//                    // mysql
//                    .or(named("execute").or(named("executeUpdate").or(named("executeQuery"))))
//                    */
//            ).intercept(
//                    // springMvc 需要使用 SpringMVC插件的Interceptor
//                    // mysql 需要使用 mysql 插件的 Interceptor
//                    MethodDelegation.to());
//            return newBuilder;
        }
    }
}
