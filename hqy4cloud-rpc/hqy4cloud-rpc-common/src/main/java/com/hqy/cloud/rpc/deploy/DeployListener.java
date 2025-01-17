package com.hqy.cloud.rpc.deploy;

import com.hqy.cloud.rpc.model.ScopeModel;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/31 17:14
 */
public interface DeployListener<T extends ScopeModel> {

    /**
     * onStarting
     * @param model deploy model
     */
    default void onStarting(T model) {}

    /**
     * onStarted
     * @param model deploy model
     */
    default void onStarted(T model) {}

    /**
     * onStopping
     * @param model deploy model
     */
    default void onStopping(T model) {}

    /**
     * onStopped
     * @param model deploy model
     */
    default void onStopped(T model) {}

    /**
     * onFailure
     * @param model deploy model
     * @param cause exception.
     */
    default void onFailure(T model, Throwable cause) {}

}
