package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.EmployeeCreateRequest;
import com.infina.hissenet.dto.request.EmployeeUpdateRequest;
import com.infina.hissenet.dto.request.ForgotPasswordRequest;
import com.infina.hissenet.dto.response.EmployeeResponse;
import com.infina.hissenet.entity.Employee;
import com.infina.hissenet.entity.Role;
import com.infina.hissenet.exception.employee.EmployeeNotFoundException;
import com.infina.hissenet.mapper.EmployeeMapper;
import com.infina.hissenet.repository.EmployeeRepository;
import com.infina.hissenet.repository.RoleRepository;
import com.infina.hissenet.service.abstracts.IEmployeeService;
import com.infina.hissenet.utils.GenericServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EmployeeService extends GenericServiceImpl<Employee, Long> implements IEmployeeService {

    private final EmployeeRepository employeeRepository;
    // private final RoleRepository roleRepository;
    private final EmployeeMapper employeeMapper;

    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
                           // RoleRepository roleRepository,
                           EmployeeMapper employeeMapper, PasswordEncoder encoder, RoleRepository roleRepository) {
        super(employeeRepository);
        this.employeeRepository = employeeRepository;
        // this.roleRepository = roleRepository;
        this.employeeMapper = employeeMapper;
        this.encoder = encoder;
        this.roleRepository = roleRepository;
    }

    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
        Employee employee = employeeMapper.toEntity(request);

        employee.setHireDate(LocalDate.now());
        employee.setPassword(encoder.encode(request.password()));

        if (request.roleIds() != null && !request.roleIds().isEmpty()) {
            List<Role> roles = roleRepository.findAllById(request.roleIds());
            employee.setRoles(Set.copyOf(roles));
        }

        if (request.createdByEmployeeId() != null) {
            Employee createdByEmployee = findById(request.createdByEmployeeId())
                    .orElseThrow(() -> new EmployeeNotFoundException(request.createdByEmployeeId()));
            employee.setCreatedBy(createdByEmployee);
        }

        Employee saved = save(employee);
        return employeeMapper.toResponse(saved);
    }

    public EmployeeResponse updateEmployee(EmployeeUpdateRequest request) {
        Employee existing = findById(request.id()).orElseThrow(() -> new EmployeeNotFoundException(request.id()));

        // Update fields from request
        if (request.firstName() != null) {
            existing.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            existing.setLastName(request.lastName());
        }
        if (request.email() != null) {
            existing.setEmail(request.email());
        }
        if (request.phone() != null) {
            existing.setPhone(request.phone());
        }
        if (request.position() != null) {
            existing.setPosition(request.position());
        }
        if (request.emergencyContactName() != null) {
            existing.setEmergencyContactName(request.emergencyContactName());
        }
        if (request.emergencyContactPhone() != null) {
            existing.setEmergencyContactPhone(request.emergencyContactPhone());
        }

        if (request.roleIds() != null) {
            System.out.println("Updating roles for employee " + existing.getId() + " with roleIds: " + request.roleIds());

            if (request.roleIds().isEmpty()) {
                System.out.println("Clearing all roles for employee " + existing.getId());
                existing.setRoles(new HashSet<>());
            } else {
                List<Role> roles = roleRepository.findAllById(request.roleIds());
                if (roles.size() != request.roleIds().size()) {
                    System.err.println("Warning: Some role IDs not found. Requested: " + request.roleIds() + ", Found: " + roles.size());
                }
                System.out.println("Setting roles: " + roles.stream().map(Role::getName).toList());
                existing.setRoles(new HashSet<>(roles));
            }
        } else {
            System.out.println("No role update requested for employee " + existing.getId());
        }

        if (request.updatedByEmployeeId() != null) {
            Employee updatedByEmployee = findById(request.updatedByEmployeeId())
                    .orElseThrow(() -> new EmployeeNotFoundException(request.updatedByEmployeeId()));
            existing.setUpdatedBy(updatedByEmployee);
        }


        Employee updated = update(existing);
        return employeeMapper.toResponse(updated);
    }

    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
        return employeeMapper.toResponse(employee);
    }

    public List<EmployeeResponse> getAllEmployees() {
        return findAll().stream().map(employeeMapper::toResponse).toList();
    }

    public void deleteEmployee(Long id) {
        Employee employee = findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
        delete(employee);
    }

    public Employee findByEmail(String email) {
        return employeeRepository.findByEmail(email).orElseThrow(() -> new EmployeeNotFoundException());
    }

    public Employee findByEmailWithRoles(String email) {
        return employeeRepository.findByEmailWithRoles(email).orElseThrow(() -> new EmployeeNotFoundException());
    }

    @Override
    public Boolean existsByEmail(String email) {
        return employeeRepository.existsByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByEmail(username);
    }
    public void changePassword(ForgotPasswordRequest request){
       Employee employee=findByEmail(request.email());
       if(request.password().equals(request.confirmNewPassword())){
           employee.setPassword(encoder.encode(request.password()));
       }
       save(employee);
    }

    public Page<EmployeeResponse> getAllEmployeesPageable(int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Employee> employeePage = findAll(pageable);

        return employeePage.map(employeeMapper::toResponse);
    }
}
