package org.example.core.match;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * 所有非 {@link NameMatch} 的情况，都要实现当前接口
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/30 10:09 PM
 */
public interface IndirectMatch extends ClassMatch {

    /**
     * {@link AgentBuilder.Default#type(ElementMatcher)} 的参数 (拦截的类范围)
     *
     * @return
     */
    ElementMatcher.Junction<? extends TypeDescription> buildJunction();
}
