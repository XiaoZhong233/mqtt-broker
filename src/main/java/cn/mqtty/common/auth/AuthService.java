/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.mqtty.common.auth;

import cn.hutool.core.util.StrUtil;
import cn.mqtty.broker.config.BrokerProperties;
import cn.mqtty.common.auth.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
