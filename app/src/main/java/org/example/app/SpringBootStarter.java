package org.example.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * SpringBoot启动类
 * <p>
 *  <ol>
 *   <li>阶段一: 初始化项目, 分别实现MySQL和SpringMVC插件, 启动时使用多个-javaagent参数</li>
 *  </ol>
 * </p>
 *
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan(basePackages = "org.example.app.mapper")
public class SpringBootStarter {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootStarter.class, args);
    }
}
