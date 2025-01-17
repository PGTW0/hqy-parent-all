package com.hqy.cloud.socketio.starter.service;

import com.corundumstudio.socketio.ex.NettyContextHelper;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.thread.ExecutorServiceProject;
import com.hqy.cloud.util.thread.ParentExecutorService;
import com.hqy.cloud.rpc.thrift.service.ThriftSocketIoPushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @see ThriftSocketIoPushService
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/22 17:55
 */
public abstract class AbstractThriftSocketIoPushService implements ThriftSocketIoPushService {

    private static final Logger log = LoggerFactory.getLogger(AbstractThriftSocketIoPushService.class);

    @Override
    public boolean syncPush(String bizId, String eventName, String wsMessageJson) {
        try {
            return NettyContextHelper.doPush(bizId, eventName, wsMessageJson);
        } catch (Exception e) {
            log.error("Failed execute to syncPush. bizId: {} | eventName: {}, wsMessageJson:{}.", bizId, eventName, wsMessageJson, e);
            return false;
        }
    }

    @Override
    public boolean syncPushMultiple(Set<String> bizIdSet, String eventName, String wsMessageJson) {
        try {
            boolean happenError = false;
            for (String bizId : bizIdSet) {
                try {
                    NettyContextHelper.doPush(bizId, eventName, wsMessageJson);
                } catch (Throwable cause) {
                    AssertUtil.isTrue(happenError, "syncPushMultiple consecutive errors, cause " + cause.getMessage());
                    happenError = true;
                    log.error("Failed execute to syncPushMultiple. bizId: {} | eventName: {}, wsMessageJson:{}.", bizId, eventName, wsMessageJson, cause);
                }
            }
        } catch (Throwable cause) {
            log.error(cause.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public void asyncPush(String bizId, String eventName, String wsMessageJson) {
        ParentExecutorService.getInstance().execute(() -> this.asyncPush(bizId, eventName, wsMessageJson), ExecutorServiceProject.PRIORITY_IMMEDIATE);
    }

    @Override
    public void asyncPushMultiple(Set<String> bizIdSet, String eventName, String wsMessageJson) {
        ParentExecutorService.getInstance().execute(() -> this.asyncPushMultiple(bizIdSet, eventName, wsMessageJson));

    }
}
