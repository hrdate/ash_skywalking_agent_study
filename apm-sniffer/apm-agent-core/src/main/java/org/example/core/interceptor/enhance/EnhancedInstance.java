package org.example.core.interceptor.enhance;

import org.example.core.AbstractClassEnhancePluginDefine;

/**
 * 所有需要增强 构造方法 or 实例方法 的字节码(类) 都会实现该接口 <br/>
 * 该接口的作用即为 {@link AbstractClassEnhancePluginDefine#CONTEXT_ATTR_NAME} (增强的字节码新增的成员变量) 产生getter, setter方法
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/31 9:45 PM
 */
public interface EnhancedInstance {
    /**
     * 为增强的字节码新增的成员变量生成 getter
     */
    Object getSkyWalkingDynamicField();
    /**
     * 为增强的字节码新增的成员变量生成 setter
     */
    void setSkyWalkingDynamicField(Object value);
}
