package com.hqy.cloud.id.service.impl;

import com.hqy.cloud.id.component.segment.service.SegmentService;
import com.hqy.cloud.id.service.RemoteLeafService;
import com.hqy.cloud.id.struct.ResultStruct;
import com.hqy.rpc.thrift.service.AbstractRPCService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * RemoteLeafServiceImpl.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/21 17:05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RemoteLeafServiceImpl extends AbstractRPCService implements RemoteLeafService {
    private final SegmentService segmentService;

    @Override
    public ResultStruct getSegmentId(String key) {
        return segmentService.get(key);
    }
}