package org.example.springmvc;

import net.bytebuddy.agent.builder.AgentBuilder;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * SpringMVC 插件(java agent入口)
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/27 3:10 PM
 */
public class PreMain {
    public static void premain(String args, Instrumentation instrumentation) {
        new AgentBuilder.Default()
                .ignore(nameEndsWith("BasicErrorController"))
                .type((isAnnotatedWith(
                        named("org.springframework.stereotype.Controller").or(
                                named("org.springframework.web.bind.annotation.RestController")))))
                .transform(new Transformer())
                .installOn(instrumentation);

    }
}
