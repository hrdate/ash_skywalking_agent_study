package org.example.core.match;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.Arrays;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * 匹配多个全限制类名的匹配器
 *
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/30 10:16 PM
 */
public class MultiClassNameMatch implements IndirectMatch {

    /**
     * 需要匹配/拦截的全限制类名集合
     */
    private List<String> needMatchClassNames;

    private MultiClassNameMatch(String... classNames) {
        if (null == classNames || 0 == classNames.length) {
            throw new IllegalArgumentException("needMatchClassNames can't be empty");
        }
        this.needMatchClassNames = Arrays.asList(classNames);
    }

    /**
     * 比如构造 {@code named("com.mysql.cj.jdbc.ClientPreparedStatement").or(named("com.mysql.cj.jdbc.ServerPreparedStatement"))} <br/>
     * 多个类之间，要求是or的关系 (即获取并集)
     */
    @Override
    public ElementMatcher.Junction<? super TypeDescription> buildJunction() {
        ElementMatcher.Junction<? super TypeDescription> junction = null;
        for (String needMatchClassName : needMatchClassNames) {
            if(null == junction) {
                junction = named(needMatchClassName);
            } else {
                junction = junction.or(named(needMatchClassName));
            }
        }
        return junction;
    }

    @Override
    public boolean isMatch(TypeDescription typeDescription) {
        // 多个类名匹配，只要含有指定的类名，就是匹配成功
        return needMatchClassNames.contains(typeDescription.getCanonicalName());
    }

    public static IndirectMatch byMultiClassMatch(String... classNames) {
        return new MultiClassNameMatch(classNames);
    }
}
