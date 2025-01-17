package com.hqy.cloud.coll.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
import com.hqy.cloud.coll.mapper.PfExceptionTkMapper;
import com.hqy.cloud.coll.entity.PfException;
import com.hqy.cloud.coll.service.PfExceptionService;
import com.hqy.cloud.rpc.thrift.struct.PageStruct;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.PERCENT;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/7 14:31
 */
@Service
public class PfExceptionServiceImpl extends BaseTkServiceImpl<PfException, Long> implements PfExceptionService {

    @Resource
    private PfExceptionTkMapper pfExceptionDao;

    @Override
    public BaseTkMapper<PfException, Long> getTkMapper() {
        return pfExceptionDao;
    }


    @Override
    public PageInfo<PfException> queryPage(String serviceName, String type, String env, String exceptionClass, String ip, String url, PageStruct struct) {
        Example example = new Example(PfException.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(serviceName)) {
            criteria.andLike("serviceName",  serviceName + PERCENT);
        }
        if (StringUtils.isNotBlank(env)) {
            criteria.andEqualTo("environment", env);
        }
        if (StringUtils.isNotBlank(type)) {
            criteria.andEqualTo("type", type);
        }
        if (StringUtils.isNotBlank(exceptionClass)) {
            criteria.andLike("exceptionClass", PERCENT + exceptionClass + PERCENT);
        }
        if (StringUtils.isNotBlank(ip)) {
            criteria.andLike("ip", PERCENT + ip + PERCENT);
        }
        if (StringUtils.isNotBlank(url)) {
            criteria.andLike("url", PERCENT + url + PERCENT);
        }
        example.orderBy("id").desc();

        PageHelper.startPage(struct.pageNumber, struct.pageSize);
        List<PfException> pfExceptions = pfExceptionDao.selectByExample(example);
        if (CollectionUtils.isEmpty(pfExceptions)) {
            return new PageInfo<>();
        }
        return  new PageInfo<>(pfExceptions);
    }
}
