package org.example.core.loader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.example.core.AbstractClassEnhancePluginDefine;
import org.example.core.PluginBoostrap;
import org.example.core.booster.AgentPackagePath;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 自定义类加载器，用于加载插件和插件内定义的拦截器
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2024/1/1 1:16 AM
 */
@Slf4j
public class AgentClassLoader extends ClassLoader {
    /**
     * 用于加载 插件jar中 {@link AbstractClassEnhancePluginDefine} 插件定义类 (jar中拦截器类由拦截的目标类对应的类加载器加载)
     */
    private static AgentClassLoader DEFAULT_LOADER;

    /**
     * 自定义类加载器 加载类的路径
     */
    private List<File> classpath;
    /**
     * 所有的插件jar包
     */
    private List<Jar> allJars;
    /**
     * 避免jar并发加载，加锁
     */
    private ReentrantLock jarScanLock = new ReentrantLock();

    public AgentClassLoader(ClassLoader parentClassLoader) {
        super(parentClassLoader);
        // 获取 agent.jar 的目录
        File agentJarDir = AgentPackagePath.getPath();
        classpath = new ArrayList<>();
        // 指定从 agent.jar 同级别目录下的子目录/plugins 加载类
        classpath.add(new File(agentJarDir, "plugins"));
    }

    public static void intDefaultLoader() {
        if (DEFAULT_LOADER == null) {
            DEFAULT_LOADER = new AgentClassLoader(PluginBoostrap.class.getClassLoader());
        }
    }

    public static AgentClassLoader getDefaultLoader() {
        return DEFAULT_LOADER;
    }

    /**
     * 双亲委派 loadClass -> 找不到 则调用当前findClass(自定义类加载逻辑) -> 最后通过defineClass获取Class对象 <br/>
     * 这里的逻辑即从 指定的插件目录加载所有的jar文件后，寻找jar中指定的 插件类 并返回其字节码
     */
    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        List<Jar> allJars = getAllJars();
        // . 转为 /， 获取类对应的文件路径
        String path = className.replace(".", "/").concat(".class");
        for (Jar jar : allJars) {
            JarEntry jarEntry = jar.jarFile.getJarEntry(path);
            if (jarEntry == null) {
                continue;
            }
            try {
                URL url = new URL("jar:file:" + jar.sourceFile.getAbsolutePath() + "!/" + path);
                byte[] classBytes = IOUtils.toByteArray(url);
                return defineClass(className, classBytes, 0, classBytes.length);
            } catch (Exception e) {
                log.error("can't find class: {}, exception: ", className, e);
            }
        }
        throw new ClassNotFoundException("can't find class: " + className);
    }

    /**
     * 解析 指定类所在的 URL (从众多插件jar中寻找)
     */
    @Override
    public URL getResource(String name) {
        List<Jar> allJars = getAllJars();
        for (Jar jar : allJars) {
            JarEntry jarEntry = jar.jarFile.getJarEntry(name);
            if (jarEntry == null) {
                continue;
            }
            try {
                return new URL("jar:file:" + jar.sourceFile.getAbsolutePath() + "!/" + name);
            } catch (MalformedURLException e) {
                log.error("getResource failed, e: ", e);
            }
        }
        return null;
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        List<URL> allResources = new ArrayList<>();
        List<Jar> allJars = getAllJars();
        log.info("allJars is not empty: {}", null!=allJars && !allJars.isEmpty());
        for (Jar jar : allJars) {
            JarEntry jarEntry = jar.jarFile.getJarEntry(name);
            if (jarEntry == null) {
                continue;
            }
            allResources.add(new URL("jar:file:" + jar.sourceFile.getAbsolutePath() + "!/" + name));
        }
        Iterator<URL> iterator = allResources.iterator();
        return new Enumeration<URL>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public URL nextElement() {
                return iterator.next();
            }
        };
    }

    private List<Jar> getAllJars() {
        // 确保仅加载一次jar
        if (allJars == null) {
            jarScanLock.lock();
            try {
                if (allJars == null) {
                    allJars = doGetJars();
                }
            } finally {
                jarScanLock.unlock();
            }
        }
        return allJars;
    }

    private List<Jar> doGetJars() {
        List<Jar> list = new LinkedList<>();
        for (File path : classpath) {
            if (path.exists() && path.isDirectory()) {
                String[] jarFileNames = path.list((dir, name) -> name.endsWith(".jar"));
                if (ArrayUtils.isEmpty(jarFileNames)) {
                    continue;
                }
                for (String jarFileName : jarFileNames) {
                    try {
                        File jarSourceFile = new File(path, jarFileName);
                        Jar jar = new Jar(new JarFile(jarSourceFile), jarSourceFile);
                        list.add(jar);
                        log.info("jar: {} loaded", jarSourceFile.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("jar: {} load failed, e: ", jarFileName, e);
                    }

                }
            }
        }
        return list;
    }

    @RequiredArgsConstructor
    private static class Jar {
        /**
         * jar文件对应的jarFile对象
         */
        private final JarFile jarFile;
        /**
         * jar文件对象
         */
        private final File sourceFile;
    }
}
