package org.example.plugins.springmvc;

import org.example.core.match.ClassAnnotationNameMatch;
import org.example.core.match.ClassMatch;

/**
 * 拦截带有{@code @RestController} 的springmvc插件
 * @author : Ashiamd email: ashiamd@foxmail.com
 * @date : 2023/12/30 11:27 PM
 */
public class RestControllerInstrumentation extends SpringmvcCommonInstrumentation {
    @Override
    protected ClassMatch enhanceClass() {
        return ClassAnnotationNameMatch.byClassAnnotationMatch("org.springframework.web.bind.annotation.RestController");
    }
}
