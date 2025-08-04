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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

        // Update roles if provided
        if (request.roleIds() != null && !request.roleIds().isEmpty()) {
            List<Role> roles = roleRepository.findAllById(request.roleIds());
            existing.setRoles(Set.copyOf(roles));
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
}
