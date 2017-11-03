package com.cfysu.ssi.dao;

import java.util.List;

/**
 * Created by cj on 2017/7/27.
 */
public interface BaseDao<E> {

    E queryForObject(Long id);

    List<E> queryByList(E entity);

    Integer getCount(E entity);

    long insert(E entity);

    int update(E entity);

    int delete(E entity);
}
