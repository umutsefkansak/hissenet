package com.infina.hissenet.utils;

import java.util.List;
import java.util.Optional;

public interface IGenericService<T,ID> {
    T save(T entity);
    T update(T entity);
    void delete(T entity);
    void deleteById(ID id);
    Optional<T> findById(ID id);
    List<T> findAll();
}
