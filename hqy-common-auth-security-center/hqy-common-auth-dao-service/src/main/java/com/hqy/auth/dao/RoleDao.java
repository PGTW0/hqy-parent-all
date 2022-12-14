package com.hqy.auth.dao;

import com.hqy.auth.entity.Role;
import com.hqy.base.BaseDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AccountRoleDao.
 * @author qiyuan.hong
 * @date 2022-03-10 21:42
 */
@Repository
public interface RoleDao extends BaseDao<Role, Integer> {

    /**
     * 根据角色名获取角色id
     * @param roleList 角色名列表
     * @return         角色id列表
     */
    List<Integer> selectIdByNames(@Param("roles") List<String> roleList);

    /**
     * 根据角色名获取角色
     * @param roles 角色名列表
     * @return      AccountRole.
     */
    List<Role> queryRolesByNames(@Param("roles")List<String> roles);

    /**
     * 获取角色列表
     * @param maxRoleLevel 角色Level
     * @param status       状态
     * @return             roles.
     */
    List<Role> queryRoles(@Param("maxRoleLevel") Integer maxRoleLevel, @Param("status") Boolean status);

    /**
     * 根据id列表查询角色列表
     * @param roleIds id列表
     * @return        roles
     */
    List<Role> queryByIds(@Param("ids") List<Integer> roleIds);

}
