package org.example.core.booster;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;

/**
 * 表示 agent.jar 所在的路径
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2024/1/1 2:01 AM
 */
@Slf4j
public class AgentPackagePath {

    /**
     * apm-agent.jar 所在路径对应的File对象
     */
    private static File AGENT_PACKAGE_PATH;

    public static File getPath() {
        return null == AGENT_PACKAGE_PATH ? AGENT_PACKAGE_PATH = findPath() : AGENT_PACKAGE_PATH;
    }

    /**
     * 获取 apm-agent.jar 所在路径对应的File对象
     */
    private static File findPath() {
        // 类名 => 路径名
        String classResourcePath = AgentPackagePath.class.getName().replaceAll("\\.", "/") + ".class";
        // file:Class文件路径.class
        // jar:file:jar文件路径.jar!/Class文件路径.class (这里因为打包，所以一定是这种情况)
        URL resource = ClassLoader.getSystemClassLoader().getResource(classResourcePath);
        if (resource != null) {
            String urlStr = resource.toString();
            // 如果是jar包中的类文件路径，则含有"!"
            boolean isInJar = urlStr.indexOf('!') > -1;
            // 因为agent会打包成jar，所以其实一定是jar里面的class类
            if (isInJar) {
                urlStr = StringUtils.substringBetween(urlStr, "file:", "!");
                File agentJarFile = null;
                try {
                    agentJarFile = new File(urlStr);
                } catch (Exception e) {
                    log.error("agent jar not find by url : {}, exception:", urlStr, e);
                }
                if (agentJarFile.exists()) {
                    // 返回jar所在的目录对应的File对象
                    return agentJarFile.getParentFile();
                }
            }
        }
        log.error("agent jar not find ");
        throw new RuntimeException("agent jar not find");
    }
}
