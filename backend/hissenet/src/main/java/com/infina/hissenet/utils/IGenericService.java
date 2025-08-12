package com.infina.hissenet.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Generic service interface providing common CRUD operations for entities.
 * This interface defines standard database operations that can be used across
 * different entity types in the application.
 *
 * @param <T> the entity type that extends BaseEntity
 * @param <ID> the type of the entity's primary key
 *
 * @author Umut Sefkan SAK
 * @version 1.0
 * @since 1.0
 */
public interface IGenericService<T,ID> {

    /**
     * Saves a given entity to the database.
     *
     * @param entity the entity to be saved, must not be null
     * @return the saved entity, will never be null
     */
    T save(T entity);

    /**
     * Updates an existing entity in the database.
     *
     * @param entity the entity to be updated, must not be null
     * @return the updated entity, will never be null
     */
    T update(T entity);

    /**
     * Performs a soft delete on the given entity by marking it as deleted.
     * This method sets the deleted flag to true instead of physically removing
     * the entity from the database.
     *
     * @param entity the entity to be soft deleted, must not be null
     */
    void delete(T entity);

    /**
     * Deletes the entity with the given id from the database.
     * This performs a hard delete, physically removing the entity.
     *
     * @param id the id of the entity to be deleted, must not be null
     */
    void deleteById(ID id);

    /**
     * Retrieves an entity by its id.
     *
     * @param id the id of the entity to retrieve, must not be null
     * @return an Optional containing the entity if found, empty Optional otherwise
     */
    Optional<T> findById(ID id);

    /**
     * Retrieves all entities from the database.
     *
     * @return a list of all entities, may be empty but never null
     */
    List<T> findAll();

    /**
     * Retrieves a page of entities from the database.
     *
     * @param pageable the pagination information including page number, size, and sort criteria
     * @return a page of entities matching the pagination criteria, never null
     */
    Page<T> findAll(Pageable pageable);

    /**
     * Checks whether an entity with the given id exists in the database.
     *
     * @param id the id to check for existence, must not be null
     * @return true if an entity with the given id exists, false otherwise
     */
    boolean existsById(ID id);
}
