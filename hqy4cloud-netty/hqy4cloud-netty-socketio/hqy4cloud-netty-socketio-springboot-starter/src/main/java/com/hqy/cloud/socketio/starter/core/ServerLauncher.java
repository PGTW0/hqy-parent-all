package com.hqy.cloud.socketio.starter.core;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.SocketIOServer;

/**
 * ServerLauncher.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/23 13:53
 */
public interface ServerLauncher {

    /**
     * create and start up SocketIoServer.
     * @param port                  server port.
     * @param contextPath           contextPath.
     * @param authorizationListener connection authorization Listener.
     * @return                      {@link SocketIOServer}
     * @throws Exception            exception.
     */
    SocketIOServer startUp(int port, String contextPath, AuthorizationListener authorizationListener) throws Exception;

}
