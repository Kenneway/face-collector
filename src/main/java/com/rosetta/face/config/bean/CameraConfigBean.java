package com.rosetta.face.config.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Component      //不加这个注解的话, 使用@Autowired 就不能注入进去了
@ConfigurationProperties(prefix = "camera")  // 配置文件中的前缀
public class CameraConfigBean {

    //接收camera里面的属性值,List中的元素是Map
    private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
}
