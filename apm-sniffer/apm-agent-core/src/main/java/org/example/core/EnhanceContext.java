package org.example.core;

/**
 * 增强类过程中的上下文状态
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/31 9:50 PM
 */
public class EnhanceContext {
    /**
     * 当前类是否被增强了
     */
    private boolean isEnhanced = false;
    /**
     * 当前类实例是否已经新增了 {@link AbstractClassEnhancePluginDefine#CONTEXT_ATTR_NAME} 成员变量
     */
    private boolean objectExtended = false;

    public boolean isEnhanced() {
        return this.isEnhanced;
    }

    public boolean isObjectExtended() {
        return this.objectExtended;
    }

    public void initializationStageCompleted() {
        isEnhanced = true;
    }

    public void objectExtendedCompleted() {
        this.objectExtended = true;
    }
}
