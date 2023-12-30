package org.example.core.match;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.Arrays;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * 同时带有指定的多个注解的匹配器
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/30 10:58 PM
 */
public class ClassAnnotationNameMatch implements IndirectMatch{
    /**
     * 需要匹配的注解集合 (所有都满足的才会视为匹配成功)
     */
    private List<String> needMatchAnnotations;

    private ClassAnnotationNameMatch(String... annotations) {
        if (null == annotations || 0 == annotations.length) {
            throw new IllegalArgumentException("annotations can't be empty");
        }
        this.needMatchAnnotations = Arrays.asList(annotations);
    }

    /**
     * 多个注解之间，要求是and的关系
     */
    @Override
    public ElementMatcher.Junction<? extends TypeDescription> buildJunction() {
        ElementMatcher.Junction<? extends TypeDescription> junction = null;
        for (String annotationName : needMatchAnnotations) {
            if(null == junction) {
                junction = isAnnotatedWith(named(annotationName));
            } else {
                junction = junction.and(isAnnotatedWith(named(annotationName)));
            }
        }
        return junction;
    }

    public static IndirectMatch byClassAnnotationMatch(String... annotationNames) {
        return new ClassAnnotationNameMatch(annotationNames);
    }
}
