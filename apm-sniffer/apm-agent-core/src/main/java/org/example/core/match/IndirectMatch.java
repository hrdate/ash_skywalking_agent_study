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
    ElementMatcher.Junction<? super TypeDescription> buildJunction();

    /**
     * 用于判断 指定的类型 是否被 匹配器匹配
     * @param typeDescription 等待判断的类型
     */
    boolean isMatch(TypeDescription typeDescription);
}
