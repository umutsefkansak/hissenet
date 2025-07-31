package com.infina.hissenet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.infina.hissenet.entity.Employee;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>{
    Optional<Employee> findByEmail(String email);
    
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.roles WHERE e.email = :email")
    Optional<Employee> findByEmailWithRoles(@Param("email") String email);
}
