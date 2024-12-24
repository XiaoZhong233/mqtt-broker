/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.mqtty.common.auth;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.mqtty.broker.config.BrokerProperties;
import cn.mqtty.broker.handler.enums.ProtocolType;
import cn.mqtty.common.auth.IAuthService;
import cn.mqtty.store.util.Base64Codec;
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
	public boolean checkValid(String username, String password, ProtocolType protocolType) {
		if(protocolType==ProtocolType.WS){
			return valid(username, password, username, password);
		}else{
			return valid(username, password, username, password);
		}

	}

	private boolean valid(String username, String password, String usernamebase64, String passwordbase64) {
		if(brokerProperties.isMqttPasswordMust()){
			if (StrUtil.isBlank(brokerProperties.getUsername()) || StrUtil.isBlank(username)) return false;
			if (StrUtil.isBlank(brokerProperties.getPassword()) || StrUtil.isBlank(password)) return false;
			return usernamebase64.equals(brokerProperties.getUsername()) && passwordbase64.equals(brokerProperties.getPassword());
		}
		return true;
	}


}
