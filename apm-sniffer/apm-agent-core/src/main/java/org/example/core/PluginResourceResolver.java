package org.example.core;

import lombok.extern.slf4j.Slf4j;
import org.example.core.loader.AgentClassLoader;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 解析 插件目录下所有 插件jar包中配置文件skywalking-plugin.def文件的URL
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2024/1/1 2:59 AM
 */
@Slf4j
public class PluginResourceResolver {
    /**
     * 获取插件目录(/plugins)下所有jar包中的skywalking-plugin.def配置文件的URL
     */
    public List<URL> getResources() {
        List<URL> cfgUrlPaths = new ArrayList<>();
        try {
            Enumeration<URL> urls = AgentClassLoader.getDefaultLoader().getResources("skywalking-plugin.def");
            while (urls.hasMoreElements()) {
                URL pluginDefineDefUrl = urls.nextElement();
                cfgUrlPaths.add(pluginDefineDefUrl);
                log.info("find skywalking plugin define file url: {}", pluginDefineDefUrl);
            }
            return cfgUrlPaths;
        } catch (Exception e) {
            log.error("read resource error", e);
        }
        return null;
    }
}
