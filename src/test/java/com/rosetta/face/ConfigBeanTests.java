package com.rosetta.face;

import com.alibaba.fastjson.JSON;
import com.rosetta.face.config.bean.FileServerConfigBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfigBeanTests {

	@Autowired
	private FileServerConfigBean fileServerConfigBean;

	@Test
	public void contextLoads() {

		String ip = fileServerConfigBean.getIp();

		System.out.println("-----------------------------" + JSON.toJSONString(fileServerConfigBean));
	}

}
