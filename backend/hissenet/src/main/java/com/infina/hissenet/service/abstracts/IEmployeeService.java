package com.infina.hissenet.service.abstracts;

import java.util.List;
import java.util.Optional;

import com.infina.hissenet.dto.request.ForgotPasswordRequest;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.infina.hissenet.dto.request.EmployeeCreateRequest;
import com.infina.hissenet.dto.request.EmployeeUpdateRequest;
import com.infina.hissenet.dto.response.EmployeeResponse;
import com.infina.hissenet.entity.Employee;

/**
 * Service interface for employee operations.
 * Handles creation, update, retrieval, deletion of employees
 * and user details service for Spring Security.
 */
public interface IEmployeeService extends UserDetailsService {

	/**
     * Creates a new employee.
     *
     * @param request employee creation data
     * @return created employee details
     */
    EmployeeResponse createEmployee(EmployeeCreateRequest request);

    /**
     * Updates an existing employee.
     *
     * @param request employee update data
     * @return updated employee details
     */
    EmployeeResponse updateEmployee(EmployeeUpdateRequest request);

    /**
     * Retrieves an employee by ID.
     *
     * @param id employee identifier
     * @return employee details
     */
    EmployeeResponse getEmployeeById(Long id);

    /**
     * Lists all employees.
     *
     * @return list of employees
     */
    List<EmployeeResponse> getAllEmployees();

    /**
     * Deletes an employee by ID.
     *
     * @param id employee identifier
     */
    void deleteEmployee(Long id);

    /**
     * Finds an employee entity by email.
     *
     * @param email employee email
     * @return employee entity
     */
    Employee findByEmail(String email);

    /**
     * Finds an employee entity by email, fetching roles eagerly.
     *
     * @param email employee email
     * @return employee entity with roles loaded
     */
    Employee findByEmailWithRoles(String email);

    Boolean existsByEmail(String email);

    void changePassword(ForgotPasswordRequest request);

    Page<EmployeeResponse> getAllEmployeesPageable(int page, int size, String sortBy, String sortDir);

    Optional<Employee> findById(Long id);
}
