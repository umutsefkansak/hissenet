package com.infina.hissenet.repository;

import com.infina.hissenet.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    boolean existsByName(String name);

    List<Role> findByIsActiveTrue();

    List<Role> findByIsActiveFalse();

    @Query("SELECT r FROM Role r WHERE r.name LIKE %:name%")
    List<Role> findByNameContaining(@Param("name") String name);

    @Query("SELECT r FROM Role r WHERE r.isActive = :isActive AND r.name LIKE %:name%")
    List<Role> findByIsActiveAndNameContaining(@Param("isActive") Boolean isActive, @Param("name") String name);
}