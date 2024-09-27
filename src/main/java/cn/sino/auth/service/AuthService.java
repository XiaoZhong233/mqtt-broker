/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.sino.auth.service;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.sino.common.auth.IAuthService;
import jakarta.annotation.PostConstruct;
import org.nutz.ioc.loader.annotation.IocBean;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;

/**
 * 用户名和密码认证服务
 */
@Component
public class AuthService implements IAuthService {

	private RSAPrivateKey privateKey;

	@Override
	public boolean checkValid(String username, String password) {
		if (StrUtil.isBlank(username)) return false;
		if (StrUtil.isBlank(password)) return false;
		return username.equals("test") && password.equals("test");
	}

//	@PostConstruct
//	public void init() {
//		privateKey = IoUtil.readObj(AuthService.class.getClassLoader().getResourceAsStream("keystore/auth-private.key"));
//	}

}
