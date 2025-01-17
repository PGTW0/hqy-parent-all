package com.hqy.account.service;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.hqy.account.struct.AccountBaseInfoStruct;
import com.hqy.account.struct.AccountStruct;
import com.hqy.account.struct.RegistryAccountStruct;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.rpc.service.RPCService;
import com.hqy.cloud.rpc.thrift.struct.CommonResultStruct;

import java.util.List;

/**
 * thrift rpc for account
 * @author qiyuan.hong
 * @date 2022-03-16 11:17
 */
@ThriftService(MicroServiceConstants.ACCOUNT_SERVICE)
public interface RemoteAccountService extends RPCService {

    /**
     * get account json info.
     * @param id  account id.
     * @return    account json info.
     */
    @ThriftMethod
    String getAccountInfoJson(@ThriftField(1) Long id);

    /**
     * 根据用户名或者邮箱获取账号id
     * @param usernameOrEmail 用户名或者邮箱
     * @return
     */
    @ThriftMethod
    Long getAccountIdByUsernameOrEmail(@ThriftField(1) String usernameOrEmail);

    /**
     * 根据用户名或者邮箱获取账号信息
     * @param usernameOrEmail 用户名或者邮箱
     * @return                AccountStruct.
     */
    @ThriftMethod
    AccountStruct getAccountStructByUsernameOrEmail(@ThriftField(1) String usernameOrEmail);


    /**
     * 获取用户基本信息
     * @param id 查找哪个用户的基本信息
     * @return   AccountBaseInfoStruct.
     */
    @ThriftMethod
    AccountBaseInfoStruct getAccountBaseInfo(@ThriftField(1)Long id);



    /**
     * 获取用户基本信息
     * @param ids 查找哪些用户的基本信息
     * @return    AccountBaseInfoStruct.
     */
    @ThriftMethod
    List<AccountBaseInfoStruct> getAccountBaseInfos(@ThriftField(1)List<Long> ids);


    /**
     * 校验用户名和邮箱是否可用
     * @param username 用户名
     * @param email    邮箱
     * @return         CommonResultStruct.
     */
    @ThriftMethod
    CommonResultStruct checkRegistryInfo(@ThriftField(1) String username, @ThriftField(2) String email);

    /**
     * 注册账号.
     * 调用此rpc默认为已经校验过邮箱和用户名是否合法.
     * 既已经调用了checkRegistryInfo方法. 校验邮箱和用户名.
     * @param struct {@link  RegistryAccountStruct}.
     * @return       CommonResultStruct.
     */
    @ThriftMethod
    CommonResultStruct registryAccount(@ThriftField(1) RegistryAccountStruct struct);

    /**
     * 修改用户密码
     * 调用此rpc默认为已经校验过用户的验证码.
     * @param usernameOrEmail 用户名或邮箱.
     * @param newPassword     新密码.
     * @return                CommonResultStruct.
     */
    @ThriftMethod
    CommonResultStruct updateAccountPassword(@ThriftField(1) String usernameOrEmail, @ThriftField(2) String newPassword);


    /**
     * 修改用户密码 并且校验旧密码是否正确
     * @param accountId   账号id
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return            CommonResultStruct.
     */
    @ThriftMethod
    CommonResultStruct updateAccountPasswordByIdAndOldPassword(Long accountId, String oldPassword, String newPassword);




}
