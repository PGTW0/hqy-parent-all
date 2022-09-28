package com.hqy.account.dto;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

/**
 * Account info DTO.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27 15:50
 */
@Data
public class AccountInfoDTO {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 拥有的权限
     */
    private String authorities;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 简介
     */
    private String intro;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 创建时间
     */
    private Date created;


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("username", username)
                .append("nickname", nickname)
                .append("phone", phone)
                .append("email", email)
                .append("authorities", authorities)
                .append("avatar", avatar)
                .append("intro", intro)
                .append("status", status)
                .append("created", created)
                .toString();
    }
}
