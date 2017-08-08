package com.cfysu.ssi.dao.impl;

import com.cfysu.ssi.dao.BaseDao;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import javax.annotation.Resource;

/**
 * Created by cj on 2017/7/27.
 */
public class BaseDaoImpl<E> implements BaseDao<E> {

    @Resource
    protected SqlMapClientTemplate sqlMapClientTemplate;

    protected String nameSpace;

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public E queryForObject(Long id) {
        return (E)sqlMapClientTemplate.queryForObject(nameSpace + ".selectByPrimaryKey", id);
    }

    public E queryForList(E entity) {
        return null;
    }

    public long insert(E entity) {
        return (Long)sqlMapClientTemplate.insert(nameSpace + ".insert",entity);
    }

    public int update(E entity) {
        return sqlMapClientTemplate.update(nameSpace + "updateById", entity);
    }

    public int delete(E entity) {
        return 0;
    }
}
