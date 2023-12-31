package org.example.core;

import org.apache.commons.lang3.StringUtils;

/**
 * 表示  skywalking-plugin.def 配置文件中的一行记录
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2024/1/1 3:36 AM
 */
public class PluginDefine {
    /**
     * 插件名，一个插件名可以对应多个类，配置中的key
     */
    private String name;

    /**
     * 插件的全限制类名
     */
    private String defineClass;

    public String getDefineClass() {
        return defineClass;
    }

    private PluginDefine(String name, String defineClass) {
        this.name = name;
        this.defineClass = defineClass;
    }

    /**
     * @param define 表示 skywalking-plugin.def 配置文件中的一行记录
     * @return PluginDefine 实例对象
     */
    public static PluginDefine build(String define) {
        if (StringUtils.isEmpty(define)) {
            throw new RuntimeException(define);
        }

        String[] pluginDefine = define.split("=");
        if (pluginDefine.length != 2) {
            throw new RuntimeException(define);
        }

        String pluginName = pluginDefine[0];
        String defineClass = pluginDefine[1];
        return new PluginDefine(pluginName, defineClass);
    }
}
