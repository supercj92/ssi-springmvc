package com.cfysu.dao.impl;

import com.cfysu.dao.BaseDao;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import javax.annotation.Resource;

/**
 * Created by cj on 2017/7/27.
 */
public class BaseDaoImpl<E, Q> implements BaseDao<E, Q> {

    @Resource
    protected SqlMapClientTemplate sqlMapClientTemplate;

    protected String nameSpace;

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }
    public E queryForObject(Long id) {
        return (E)sqlMapClientTemplate.queryForObject(nameSpace + ".selectByPrimaryKey", id);
    }

    public E queryForList(Q query) {
        return null;
    }

    public long insert(E entity) {
        return 0;
    }

    public long update(Object query) {
        return 0;
    }

    public long delete(Object query) {
        return 0;
    }
}
