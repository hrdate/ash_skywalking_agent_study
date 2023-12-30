package org.example.core.match;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * 表示类的匹配范围 的 顶级接口 <br/>
 * 相当于对应 {@link AgentBuilder#type(ElementMatcher)} 的 参数 {@code ElementMatcher<? super TypeDescription>} <br/>
 * <ol>
 *  <li>{@link NameMatch} 全限制类名匹配器</li>
 *  <li>{@link IndirectMatch} 除了全限制类名外的其他匹配方式 间接匹配器</li>
 * </ol>
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/30 6:16 PM
 */
public interface ClassMatch {
}
