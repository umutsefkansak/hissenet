package com.infina.hissenet.repository;

import com.infina.hissenet.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Role entity operations.
 * Manages user roles and permissions in the system.
 *
 * @author Umut Sefkan SAK
 * @version 1.0
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a role by its name.
     *
     * @param name the role name
     * @return optional containing the role if found
     */
    Optional<Role> findByName(String name);

    /**
     * Checks if a role name already exists.
     *
     * @param name the role name to check
     * @return true if role name exists
     */
    boolean existsByName(String name);



    /**
     * Finds all active roles.
     *
     * @return list of active roles
     */
    List<Role> findByIsActiveTrue();


    /**
     * Finds all inactive roles.
     *
     * @return list of inactive roles
     */
    List<Role> findByIsActiveFalse();



    /**
     * Finds roles containing the specified name pattern.
     *
     * @param name the name pattern to search for
     * @return list of roles matching the pattern
     */
    @Query("SELECT r FROM Role r WHERE r.name LIKE %:name%")
    List<Role> findByNameContaining(@Param("name") String name);


    /**
     * Finds roles by active status and name pattern.
     *
     * @param isActive the active status filter
     * @param name the name pattern to search for
     * @return list of roles matching both criteria
     */
    @Query("SELECT r FROM Role r WHERE r.isActive = :isActive AND r.name LIKE %:name%")
    List<Role> findByIsActiveAndNameContaining(@Param("isActive") Boolean isActive, @Param("name") String name);
}