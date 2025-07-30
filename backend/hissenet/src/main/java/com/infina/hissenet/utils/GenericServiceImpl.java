package com.infina.hissenet.utils;

import com.infina.hissenet.entity.base.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public class GenericServiceImpl<T extends BaseEntity, ID> implements IGenericService<T, ID> {

    private final JpaRepository<T, ID> repository;

    public GenericServiceImpl(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }


    @Override
    public T update(T entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(T entity) {
        entity.setDeleted(true);
        update(entity);
    }

    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return repository.findAll();
    }
    @Override
    @Transactional(readOnly = true)
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }
}
