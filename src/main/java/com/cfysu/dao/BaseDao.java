package com.cfysu.dao;

/**
 * Created by cj on 2017/7/27.
 */
public interface BaseDao<E, Q> {

    E queryForObject(Long id);

    E queryForList(Q query);

    long insert(E entity);

    long update(Q query);

    long delete(Q query);
}
