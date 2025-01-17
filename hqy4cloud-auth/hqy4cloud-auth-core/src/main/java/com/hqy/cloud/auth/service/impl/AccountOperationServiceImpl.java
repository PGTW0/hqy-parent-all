package com.hqy.cloud.auth.service.impl;

import cn.hutool.core.util.StrUtil;
import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.cloud.auth.base.dto.UserDTO;
import com.hqy.cloud.auth.entity.Account;
import com.hqy.cloud.auth.entity.AccountProfile;
import com.hqy.cloud.auth.entity.AccountRole;
import com.hqy.cloud.auth.entity.Role;
import com.hqy.cloud.auth.service.AccountOperationService;
import com.hqy.cloud.auth.service.tk.*;
import com.hqy.cloud.auth.utils.AvatarHostUtil;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.foundation.id.DistributedIdGen;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.cloud.util.spring.SpringContextHolder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.COMMA;
import static com.hqy.cloud.common.result.ResultCode.INVALID_UPLOAD_FILE;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 15:43
 */
@Service
@RequiredArgsConstructor
public class AccountOperationServiceImpl implements AccountOperationService {

    private static final Logger log = LoggerFactory.getLogger(AccountOperationServiceImpl.class);

    private final PasswordEncoder passwordEncoder;
    private final AccountTkService accountTkService;
    private final AccountProfileTkService accountProfileTkService;
    private final RoleTkService roleTkService;
    private final AccountRoleTkService accountRoleTkService;
    private final SysOauthClientTkService sysOauthClientTkService;
    private final TransactionTemplate transactionTemplate;


    @Override
    public AccountInfoDTO getAccountInfo(Long id) {
        AssertUtil.notNull(id, "Account id should not be null.");
        AccountInfoDTO accountInfo = accountTkService.getAccountInfo(id);
        if (Objects.isNull(accountInfo)) {
            return null;
        }
        AvatarHostUtil.settingAvatar(accountInfo);
        return accountInfo;
    }


    @Override
    public List<AccountInfoDTO> getAccountInfo(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<AccountInfoDTO> accountInfos = accountTkService.getAccountInfos(ids);
        if (CollectionUtils.isNotEmpty(accountInfos)) {
            accountInfos = accountInfos.stream().peek(AvatarHostUtil::settingAvatar).collect(Collectors.toList());
        }
        return accountInfos;
    }

    @Override
    public boolean checkParamExist(String username, String email, String phone) {
        if (StringUtils.isAllEmpty(username, email, phone)) {
            return true;
        }
        Account account = new Account();
        if (StringUtils.isNotBlank(username)) {
            account.setUsername(username);
        }
        if (StringUtils.isNotBlank(email)) {
            account.setUsername(email);
        }
        if (StringUtils.isNotBlank(phone)) {
            account.setUsername(phone);
        }
        return CollectionUtils.isNotEmpty(accountTkService.queryList(account));
    }

    @Override
    public boolean registryAccount(UserDTO userDTO, List<Role> roles) {
        Account account = buildAccount(userDTO, roles);
        List<AccountRole> accountRoles = buildAccountRole(account, roles);
        AccountProfile accountProfile = buildAccountProfile(account, userDTO);

        Boolean result = transactionTemplate.execute(status -> {
            try {
                AssertUtil.isTrue(accountTkService.insert(account), "Failed execute to insert Account: " + account);
                AssertUtil.isTrue(accountRoleTkService.insertList(accountRoles), "Failed execute to insert accountRoles, data: " + JsonUtil.toJson(accountRoles));
                AssertUtil.isTrue(accountProfileTkService.insert(accountProfile), "Failed execute to insert account profile, data: " + JsonUtil.toJson(accountProfile));
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.error(cause.getMessage(), cause);
                return false;
            }
        });

        return Boolean.TRUE.equals(result);
    }

    private Account buildAccount(UserDTO userDTO, List<Role> roles) {
        List<String> roleNames = roles.stream().map(Role::getName).collect(Collectors.toList());
        String role = StrUtil.join(COMMA, roleNames);
        Account account = new Account(DistributedIdGen.getSnowflakeId(), userDTO.getUsername(), passwordEncoder.encode(userDTO.getPassword()), userDTO.getEmail(), role, userDTO.getPhone());
        if (Objects.nonNull(userDTO.getStatus())) {
            account.setStatus(userDTO.getStatus());
        }
        return account;
    }

    private List<AccountRole> buildAccountRole(Account account, List<Role> roles) {
        return roles.stream().map(e -> new AccountRole(account.getId(), e.getId(), e.getLevel())).collect(Collectors.toList());
    }

    private AccountProfile buildAccountProfile(Account account, UserDTO userDTO) {
        return new AccountProfile(account.getId(), userDTO.getNickname(), userDTO.getAvatar());
    }

    @Override
    public boolean deleteAccountRole(Role role) {
        List<AccountRole> accountRoles = accountRoleTkService.queryList(new AccountRole(role.getId()));
        Boolean result = transactionTemplate.execute(status -> {
            try {
                role.setDeleted(true);
                AssertUtil.isTrue(roleTkService.update(role), ResultCode.SYSTEM_ERROR_UPDATE_FAIL.message);
                AssertUtil.isTrue(accountRoleTkService.deleteByAccountRoles(accountRoles), "Failed execute to delete account role.");
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                return false;
            }
        });

        return Boolean.TRUE.equals(result);
    }


    @Override
    public boolean editAccount(UserDTO userDTO, List<Role> roles, Account account, List<Role> oldRoles) {
        // update account.
        setAccountInfo(account, userDTO, roles);
        Boolean result = transactionTemplate.execute(status -> {
            try {
                AssertUtil.isTrue(accountTkService.update(account), INVALID_UPLOAD_FILE.message);
                if (CollectionUtils.isNotEmpty(oldRoles)) {
                    AssertUtil.isTrue(accountRoleTkService.delete(new AccountRole(account.getId())), "Failed execute to delete old account roles.");
                    AssertUtil.isTrue(accountRoleTkService.insertList(buildAccountRole(account, roles)), "Failed execute to insert new account roles.");
                }
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.error(cause.getMessage(), cause);
                return false;
            }
        });
        SpringContextHolder.getBean(AccountBaseInfoCacheDataServiceService.class).invalid(account.getId());
        return Boolean.TRUE.equals(result);
    }

    @Override
    public boolean deleteUser(Account account) {
        account.setDeleted(true);
        List<AccountRole> accountRoles = accountRoleTkService.queryList(new AccountRole(account.getId()));
        Boolean result = transactionTemplate.execute(status -> {
            try {
                AssertUtil.isTrue(accountTkService.update(account), "Failed execute to update account.");
                if (CollectionUtils.isNotEmpty(accountRoles)) {
                    AssertUtil.isTrue(accountRoleTkService.deleteByAccountRoles(accountRoles), "Failed execute to deleted account roles.");
                }
                return true;
            } catch (Throwable cause) {
                status.setRollbackOnly();
                log.error(cause.getMessage(), cause);
                return false;
            }
        });
        return Boolean.TRUE.equals(result);
    }

    private void setAccountInfo(Account account, UserDTO userDTO, List<Role> roles) {
        if (StrUtil.isNotBlank(userDTO.getPassword())) {
            account.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        if (StrUtil.isNotBlank(userDTO.getEmail())) {
            account.setEmail(userDTO.getEmail());
        }
        if (StrUtil.isNotBlank(account.getUsername())) {
            account.setUsername(account.getUsername());
        }
        if (StrUtil.isNotBlank(account.getPhone())) {
            account.setPhone(account.getPhone());
        }
        account.setStatus(userDTO.getStatus());
        List<String> roleNames = roles.stream().map(Role::getName).collect(Collectors.toList());
        account.setRoles(StrUtil.join(COMMA, roleNames));
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
    public RoleTkService getRoleTkService() {
        return roleTkService;
    }

    @Override
    public AccountRoleTkService getAccountRoleTkService() {
        return accountRoleTkService;
    }
}
