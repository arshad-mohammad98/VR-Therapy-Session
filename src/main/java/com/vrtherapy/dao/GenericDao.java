package com.vrtherapy.dao;

import java.util.List;
import java.util.Optional;

/**
 * Generic Data Access Object contract demonstrating Core Java Generics and Collections.
 *
 * @param <T>  Entity domain type
 * @param <ID> Unique identifier type (typically Long)
 */
public interface GenericDao<T, ID> {

    /**
     * Finds an entity by its primary key ID wrapped in an Optional.
     */
    Optional<T> findById(ID id);

    /**
     * Retrieves all persistent records as an unmodifiable or mutable List.
     */
    List<T> findAll();

    /**
     * Persists a new entity record, populating its generated ID.
     */
    T save(T entity);

    /**
     * Updates an existing entity record in the database.
     */
    void update(T entity);

    /**
     * Deletes an entity record by primary key ID.
     */
    void deleteById(ID id);

    /**
     * Returns total count of records in the underlying table.
     */
    long count();
}
