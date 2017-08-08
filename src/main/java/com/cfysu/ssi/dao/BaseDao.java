package com.cfysu.ssi.dao;

/**
 * Created by cj on 2017/7/27.
 */
public interface BaseDao<E> {

    E queryForObject(Long id);

    E queryForList(E entity);

    long insert(E entity);

    int update(E entity);

    int delete(E entity);
}
