package org.example.core;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.example.core.match.ClassMatch;
import org.example.core.match.IndirectMatch;
import org.example.core.match.NameMatch;

import java.util.*;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * 用于查找/dist/plugins下的插件
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/31 1:28 PM
 */
public class PluginFinder {

    /**
     * 用于存储 {@link org.example.core.match.NameMatch} 类型的插件 <br/>
     * key: 匹配的全限制类名, value: 插件plugin集合
     */
    private final Map<String, LinkedList<AbstractClassEnhancePluginDefine>> nameMatchDefine = new HashMap<>();

    /**
     * 存储 {@link org.example.core.match.IndirectMatch} 类型的插件 <br/>
     */
    private final List<AbstractClassEnhancePluginDefine> signatureMatchDefine = new ArrayList<>();

    /**
     * 对/dist/plugins 目录下的插件进行分类
     *
     * @param plugins 插件集合
     */
    public PluginFinder(List<AbstractClassEnhancePluginDefine> plugins) {
        for (AbstractClassEnhancePluginDefine plugin : plugins) {
            ClassMatch classMatch = plugin.enhanceClass();
            if (null == classMatch) {
                continue;
            }
            // 1. 全限制类名精准匹配
            if (classMatch instanceof NameMatch) {
                NameMatch nameMatch = (NameMatch) classMatch;
                nameMatchDefine.computeIfAbsent(nameMatch.getClassName(), item -> new LinkedList<>()).add(plugin);
            } else {
                // 2. 间接匹配 (本身目前的 ClassMatch实现中, 就两大类: 类名匹配, 间接匹配)
                signatureMatchDefine.add(plugin);
            }
        }
    }

    /**
     * 构造 {@link AgentBuilder#type(ElementMatcher)} 的参数, 即需要拦截的类范围 <br/>
     * 多个插件插件的类范围用or链接，表示取并集，所有插件总计需要拦截的类范围
     */
    public ElementMatcher<? super TypeDescription> buildMatch() {
        // 1. 先判断 全限制类名 匹配
        ElementMatcher.Junction<? super TypeDescription> junction = new ElementMatcher.Junction.AbstractBase<NamedElement>() {
            @Override
            public boolean matches(NamedElement target) {
                // 某个类首次被加载时回调当前逻辑
                return nameMatchDefine.containsKey(target.getActualName());
            }
        };
        // 2. 接着判断是否命中 间接匹配 (这里只增强 非 接口类)
        junction.and(not(isInterface()));
        for (AbstractClassEnhancePluginDefine pluginDefine : signatureMatchDefine) {
            ClassMatch classMatch = pluginDefine.enhanceClass();
            if (classMatch instanceof IndirectMatch) {
                // 实际上运行到这里，signatureMatchDefine中的一定时IndirectMatch
                IndirectMatch indirectMatch = (IndirectMatch) classMatch;
                // 用or链接，即取所有插件 需要拦截的类范围 并集
                junction = junction.or(indirectMatch.buildJunction());
            }
        }
        return junction;
    }

    /**
     * 从pluginFinder维护的 类=>插件 关系(插件内有 类 => 拦截点(方法范围, 拦截器)) 中搜索 当前类对应的插件集合
     *
     * @param typeDescription 被匹配到的类 (某个插件声明拦截的类)
     * @return 拦截当前指定类的插件集合
     */
    public List<AbstractClassEnhancePluginDefine> find(TypeDescription typeDescription) {
        List<AbstractClassEnhancePluginDefine> matechedPluginList = new LinkedList<>();
        String className = typeDescription.getTypeName();
        // 1. 判断 全限制类名匹配的插件
        if(nameMatchDefine.containsKey(className)){
            matechedPluginList.addAll(nameMatchDefine.get(className));
        }
        // 2. 判断 间接匹配的插件 (需要插件提供 判断是否匹配的方法)
        for(AbstractClassEnhancePluginDefine indirectMatchPlugin : signatureMatchDefine) {
            IndirectMatch indirectMatch = (IndirectMatch) indirectMatchPlugin.enhanceClass();
            if(indirectMatch.isMatch(typeDescription)) {
                matechedPluginList.add(indirectMatchPlugin);
            }
        }
        return matechedPluginList;
    }
}
