package com.hqy.cloud.rpc.config.deploy;

import com.hqy.cloud.rpc.server.RPCServer;
import com.hqy.cloud.rpc.service.RPCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AbstractRPCServer.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/1 15:50
 */
public abstract class AbstractRPCServer implements RPCServer {
    private final Logger log = LoggerFactory.getLogger(AbstractRPCServer.class);

    private final List<RPCService> rpcServices;
    private final AtomicBoolean destroyed = new AtomicBoolean(false);

    public AbstractRPCServer(List<RPCService> rpcServices) {
        this.rpcServices = rpcServices;
    }

    @Override
    public void destroy() {
        if (destroyed.compareAndSet(false, true)) {
            try {
                doDestroy();
            } catch (Throwable cause) {
                log.error("Failed execute to destroy rpc server.", cause);
            }
        }
    }

    /**
     * do destroy.
     */
    protected abstract void doDestroy();

    @Override
    public List<RPCService> getRegistryRpcServices() {
        return this.rpcServices;
    }

    @Override
    public boolean isDestroy() {
        return destroyed.get();
    }

    @Override
    public boolean isAvailable() {
        return !destroyed.get();
    }


}
