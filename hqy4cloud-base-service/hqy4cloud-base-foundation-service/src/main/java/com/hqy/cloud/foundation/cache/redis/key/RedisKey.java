package com.hqy.cloud.foundation.cache.redis.key;


import org.apache.commons.lang3.StringUtils;

import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.UNION;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/6 16:40
 */
public interface RedisKey {


    /**
     * 获取redis key
     * @return redis key
     * @throws UnsupportedOperationException 不支持的操作.
     */
     String getKey() throws UnsupportedOperationException;

    /**
     * 获取redis key
     * @param key 进行拼接的key
     * @return redis key.
     */
    default String getKey(String key) {
        try {
            String prefix = getKey();
            if (StringUtils.isBlank(prefix)) {
                return key;
            }
            return prefix + UNION + key;
        } catch (Throwable cause) {
            return key;
        }
    }



}
