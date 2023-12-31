package org.example.core.match;

/**
 * 专门用于单个全限制类名匹配的匹配器, 仅仅适用于 {@link net.bytebuddy.matcher.ElementMatchers#named(String)}
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/30 10:06 PM
 */
public class NameMatch implements ClassMatch {
    /**
     * 需要匹配的全限制类名
     */
    private String className;

    private NameMatch(String className) {this.className = className;}

    public String getClassName() {return this.className;}

    public static NameMatch byClassName(String className) {return new NameMatch(className);}
}
