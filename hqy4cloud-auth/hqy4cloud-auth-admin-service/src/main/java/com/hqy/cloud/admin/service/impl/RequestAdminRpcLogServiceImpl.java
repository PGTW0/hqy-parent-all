package com.hqy.cloud.admin.service.impl;

import com.hqy.cloud.admin.service.RequestAdminRpcLogService;
import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.common.bind.MessageResponse;
import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.coll.service.RpcLogRemoteService;
import com.hqy.coll.struct.PageRpcExceptionRecordStruct;
import com.hqy.coll.struct.PageRpcFlowRecordStruct;
import com.hqy.rpc.nacos.client.starter.RPCClient;
import com.hqy.rpc.thrift.struct.PageStruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/9 14:15
 */
@Service
@RequiredArgsConstructor
public class RequestAdminRpcLogServiceImpl implements RequestAdminRpcLogService {


    @Override
    public DataResponse queryRpcFlowPage(String caller, String provider, Integer current, Integer size) {
        RpcLogRemoteService service = RPCClient.getRemoteService(RpcLogRemoteService.class);
        PageRpcFlowRecordStruct pageRpcFlowRecordStruct = service.pageRpcFlowLog(caller, provider, new PageStruct(current, size));
        return CommonResultCode.dataResponse(pageRpcFlowRecordStruct);
    }

    @Override
    public MessageResponse deleteRpcFlowRecord(Long id) {
        RpcLogRemoteService service = RPCClient.getRemoteService(RpcLogRemoteService.class);
        service.deleteRpcFlowLogRecord(id);
        return CommonResultCode.messageResponse();
    }

    @Override
    public DataResponse queryRpcErrorPage(String application, String serviceClassName, Integer type, Integer current, Integer size) {
        RpcLogRemoteService service = RPCClient.getRemoteService(RpcLogRemoteService.class);
        PageRpcExceptionRecordStruct pageRpcExceptionRecordStruct = service.pageRpcErrorLog(application, serviceClassName, type, new PageStruct(current, size));
        return CommonResultCode.dataResponse(pageRpcExceptionRecordStruct);
    }

    @Override
    public MessageResponse deleteRpcExceptionRecord(Long id) {
        RpcLogRemoteService service = RPCClient.getRemoteService(RpcLogRemoteService.class);
        service.deleteRpcExceptionRecord(id);
        return CommonResultCode.messageResponse();
    }
}