/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package cn.mqtty.common.subscribe;

import java.util.List;
import java.util.Set;

/**
 * 订阅存储服务接口
 */
public interface ISubscribeStoreService {

	/**
	 * 存储订阅
	 */
	void put(String topicFilter, SubscribeStore subscribeStore);

	/**
	 * 删除订阅
	 */
	void remove(String topicFilter, String clientId);

	/**
	 * 删除clientId的订阅
	 */
	void removeForClient(String clientId);

	/**
	 * 获取订阅存储集
	 */
	List<SubscribeStore> search(String topic);

	/**
	 * 获取特定的订阅存储集
	 * 不支持通配符
	 */
	List<SubscribeStore> searchSpecific(String topic);

	/**
	 * 获取 clientId 所订阅的 topic 集合
	 *
	 * @param clientId 客户端ID
	 * @return 客户端订阅的 topic 集合
	 */
	Set<String> getSubscribedTopics(String clientId);
}
