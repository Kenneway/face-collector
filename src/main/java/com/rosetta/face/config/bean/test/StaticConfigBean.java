package com.rosetta.face.config.bean.test;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Component      //不加这个注解的话, 使用@Autowired 就不能注入进去了
@ConfigurationProperties(prefix = "static")  // 配置文件中的前缀
public class StaticConfigBean {

    private String ffaviconRepoContext;

    private String ffaviconRepoDir;

    private String ffaviconRepoUrl;

    private String ffaviconDefaultUrl;

    private String ffaceRepoDir;
}
