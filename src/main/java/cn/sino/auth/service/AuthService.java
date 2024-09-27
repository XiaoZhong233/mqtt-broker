/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.sino.auth.service;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.sino.broker.config.BrokerProperties;
import cn.sino.common.auth.IAuthService;
import jakarta.annotation.PostConstruct;
import org.nutz.ioc.loader.annotation.IocBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;

/**
 * 用户名和密码认证服务
 */
@Component
public class AuthService implements IAuthService {

	@Autowired
	BrokerProperties brokerProperties;


	@Override
	public boolean checkValid(String username, String password) {
		if(brokerProperties.isMqttPasswordMust()){
			if (StrUtil.isBlank(brokerProperties.getUsername())) return false;
			if (StrUtil.isBlank(brokerProperties.getPassword())) return false;
			return username.equals(brokerProperties.getUsername()) && password.equals(brokerProperties.getUsername());
		}
		return true;
	}


}
