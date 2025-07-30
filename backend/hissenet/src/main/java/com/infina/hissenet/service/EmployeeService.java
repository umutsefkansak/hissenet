package com.infina.hissenet.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.infina.hissenet.dto.request.EmployeeCreateRequest;
import com.infina.hissenet.dto.request.EmployeeUpdateRequest;
import com.infina.hissenet.dto.response.EmployeeResponse;
import com.infina.hissenet.entity.Employee;
import com.infina.hissenet.exception.EmployeeNotFoundException;
import com.infina.hissenet.mapper.EmployeeMapper;
import com.infina.hissenet.repository.EmployeeRepository;
import com.infina.hissenet.utils.GenericServiceImpl;

@Service
public class EmployeeService extends GenericServiceImpl<Employee, Long> {

	private final EmployeeRepository employeeRepository;
	// private final RoleRepository roleRepository;
	private final EmployeeMapper employeeMapper;

	public EmployeeService(EmployeeRepository employeeRepository,
			// RoleRepository roleRepository,
			EmployeeMapper employeeMapper) {
		super(employeeRepository);
		this.employeeRepository = employeeRepository;
		// this.roleRepository = roleRepository;
		this.employeeMapper = employeeMapper;
	}

	public EmployeeResponse createEmployee(EmployeeCreateRequest request) {

		Employee employee = employeeMapper.toEntity(request);

		employee.setHireDate(LocalDate.now());

		if (request.roleIds() != null && !request.roleIds().isEmpty()) {
			// List<Role> roles = roleRepository.findAllById(request.roleIds());
			// employee.setRoles(Set.copyOf(roles));
		}

		Employee saved = save(employee);
		return employeeMapper.toResponse(saved);
	}

	public EmployeeResponse updateEmployee(EmployeeUpdateRequest request) {
		Employee existing = findById(request.id()).orElseThrow(() -> new EmployeeNotFoundException(request.id()));

		employeeMapper.toEntity(request);

		// Rolleri güncellemek isterseniz RoleRepository'yi servise yeniden ekleyin:
		/*
		 * if (request.roleIds() != null) { List<Role> roles =
		 * roleRepository.findAllById(request.roleIds());
		 * existing.setRoles(Set.copyOf(roles)); }
		 */

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

}
