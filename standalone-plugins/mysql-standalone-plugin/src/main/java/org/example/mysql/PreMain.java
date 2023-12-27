package org.example.mysql;

import net.bytebuddy.agent.builder.AgentBuilder;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * MySQL插件(java agent入口)
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/27 3:10 PM
 */
public class PreMain {
    public static void premain(String args, Instrumentation instrumentation) {
        new AgentBuilder.Default()
                .type(named("com.mysql.cj.jdbc.ClientPreparedStatement")
                        .or(named("com.mysql.cj.jdbc.ServerPreparedStatement")))
                .transform(new Transformer())
                .installOn(instrumentation);

    }
}
