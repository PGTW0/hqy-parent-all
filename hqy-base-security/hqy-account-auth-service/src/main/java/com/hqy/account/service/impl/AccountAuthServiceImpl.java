package com.hqy.account.service.impl;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.account.service.AccountAuthService;
import com.hqy.account.service.AccountOauthClientTkService;
import com.hqy.account.service.AccountProfileTkService;
import com.hqy.account.service.AccountTkService;
import com.hqy.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Account Auth Service Crud.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 15:43
 */
@Service
@RequiredArgsConstructor
public class AccountAuthServiceImpl implements AccountAuthService {
    private static final Logger log = LoggerFactory.getLogger(AccountAuthServiceImpl.class);

    private final AccountTkService accountTkService;
    private final AccountProfileTkService accountProfileTkService;
    private final AccountOauthClientTkService accountOauthClientTkService;


    @Override
    public AccountInfoDTO getAccountInfo(Long id) {
        AssertUtil.notNull(id, "Account id should not be null.");
        return accountTkService.getAccountInfo(id);
    }

    @Override
    public AccountTkService getAccountTkService() {
        return accountTkService;
    }

    @Override
    public AccountProfileTkService getAccountProfileTkService() {
        return accountProfileTkService;
    }

    @Override
    public AccountOauthClientTkService getAccountOauthClientTkService() {
        return accountOauthClientTkService;
    }
}
