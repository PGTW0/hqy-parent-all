package com.hqy.base;

import java.util.List;

/**
 * 基于tk的单表crud service
 * T为对应的Entity, PK为主键类型
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/1 17:46
 */
public interface BaseTkService <T extends BaseEntity<PK>, PK> {

    /**
     * 根据id查找
     * @param pk primary key
     * @return 实体Entity
     */
    T queryById(PK pk);


    /**
     * 根据实体属性查找 t不能为null
     * @param t 实体Entity
     * @return 返回唯一确定的一行数据
     */
    T queryOne(T t);


    /**
     * 根据实体属性查找 t不能为null
     * @param t 实体Entity
     * @return 返回实体的集合
     */
    List<T> queryList(T t);

    /**
     * 查询所有
     * @return 查询表中所有的数据.
     */
    List<T> queryAll();


    /**
     * 插入一行数据到数据库
     * @param t 实体对象
     * @return 是否插入数据库成功
     */
    boolean insert(T t);


    /**
     * 插入一行数据到数据库 并且返回主键
     * @param t
     * @return
     */
    PK insertReturnPk(T t);


    /**
     * 批量插入数据到数据库
     * @param entities 数据列表
     * @return 是否插入数据库成功
     */
    boolean insertList(List<T> entities);


    /**
     * 修改数据库的数据
     * @param t 实体对象
     * @return 是否修改数据成功
     */
    boolean update(T t);


    /**
     * 根据id删除数据
     * @param pk 主键id
     * @return 是否删除成功
     */
    boolean deleteByPrimaryKey(PK pk);

    /**
     * 根据实体属性作为条件进行删除，查询条件使用等号
     * @param t t   实体对象
     * @return      是否删除数据成功
     */
    boolean delete(T t);

    BaseDao<T, PK> getDao();


}
