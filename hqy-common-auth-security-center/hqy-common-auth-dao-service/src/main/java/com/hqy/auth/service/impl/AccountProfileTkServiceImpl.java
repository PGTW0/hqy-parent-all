package com.hqy.auth.service.impl;

import com.hqy.auth.dao.AccountProfileDao;
import com.hqy.auth.entity.AccountProfile;
import com.hqy.auth.service.AccountProfileTkService;
import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 15:40
 */
@Service
@RequiredArgsConstructor
public class AccountProfileTkServiceImpl extends BaseTkServiceImpl<AccountProfile, Long> implements AccountProfileTkService {

    private final AccountProfileDao accountProfileDao;

    @Override
    public BaseDao<AccountProfile, Long> getTkDao() {
        return accountProfileDao;
    }
}