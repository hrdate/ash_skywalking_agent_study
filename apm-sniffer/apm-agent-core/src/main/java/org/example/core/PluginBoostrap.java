package org.example.core;

import lombok.extern.slf4j.Slf4j;
import org.example.core.loader.AgentClassLoader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 加载jar文件中的 插件类
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2024/1/1 1:18 AM
 */
@Slf4j
public class PluginBoostrap {
    /**
     * 加载所有生效的插件(即指定目录下的所有插件jar文件)
     * <ol>
     *   <li>获取指定存放插件jar文件的路径</li>
     *   <li>使用自定义类加载器进行加载(默认的类加载器不会从我们指定的路径加载class)</li>
     * </ol>
     */
    public List<AbstractClassEnhancePluginDefine> loadPlugins() {
        AgentClassLoader.intDefaultLoader();
        PluginResourceResolver resourceResolver = new PluginResourceResolver();
        // 1. 获取插件jar中配置文件skywalking-plugin.def的 URL集合
        List<URL> resources = resourceResolver.getResources();
        if (null == resources || resources.isEmpty()) {
            log.warn("don't find any skywalking-plugin.def");
            return new ArrayList<>();
        }
        // 2. 存储所有插件jar中的 skywalking-plugin.def 的配置行记录
        for (URL resource : resources) {
            try {
                PluginCfg.INSTANCE.load(resource.openStream());
            } catch (Exception e) {
                log.error("plugin def file {} init fail", resource, e);
            }
        }
        // 3. 根据配置行中记录的全限制类名加载 插件类
        List<PluginDefine> pluginClassList = PluginCfg.INSTANCE.getPluginClassList();
        List<AbstractClassEnhancePluginDefine> plugins = new ArrayList<>();
        for (PluginDefine pluginDefine : pluginClassList) {
            try {
                // 注意这里使用自定义的类加载器 AgentClassLoader，因为只有这个类加载器知道从我们定义的路径寻找class文件
                AbstractClassEnhancePluginDefine plugin = (AbstractClassEnhancePluginDefine) Class.forName(pluginDefine.getDefineClass(),
                        true, AgentClassLoader.getDefaultLoader()).newInstance();
                plugins.add(plugin);
            } catch (Exception e) {
                log.error("load class {} failed, e: ", pluginDefine.getDefineClass(), e);
            }
        }
        return plugins;
    }
}
