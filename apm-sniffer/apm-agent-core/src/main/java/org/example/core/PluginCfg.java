package org.example.core;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 表示 skywalking-plugin.def 配置
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2024/1/1 3:34 AM
 */
@Slf4j
public enum PluginCfg {
    /**
     * 单例
     */
    INSTANCE;

    /**
     * 所有插件的skywalking-plugin.def文件解析出来的PluginDefine实例 （也就是多行配置记录key=value）
     */
    private List<PluginDefine> pluginClassList = new ArrayList<>();

    /**
     * 转换skywalking-plugin.def文件的内容为PluginDefine
     */
    void load(InputStream input) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String pluginDefine;
            // 读取多行配置，构造 配置行对象
            while ((pluginDefine = reader.readLine()) != null) {
                try {
                    if (pluginDefine.trim().isEmpty() || pluginDefine.startsWith("#")) {
                        continue;
                    }
                    PluginDefine plugin = PluginDefine.build(pluginDefine);
                    pluginClassList.add(plugin);
                } catch (Exception e) {
                    log.error("Failed to format plugin({}) define, e :", pluginDefine, e);
                }
            }
        }
    }

    public List<PluginDefine> getPluginClassList() {
        return pluginClassList;
    }
}
