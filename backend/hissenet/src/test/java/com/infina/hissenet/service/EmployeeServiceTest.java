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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock private EmployeeRepository employeeRepository;
    @Mock private EmployeeMapper employeeMapper;
    @Mock private PasswordEncoder encoder;
    @Mock private RoleRepository roleRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private EmployeeCreateRequest buildCreateReq(Set<Long> roleIds, Long createdById) {
        return new EmployeeCreateRequest(
                "John", "Doe", "john@example.com", "+905551112233", "Developer",
                "Password1", "Jane Doe", "+905559998877", roleIds, createdById
        );
    }

    private EmployeeUpdateRequest buildUpdateReq(Long id, Set<Long> roleIds, Long updatedById) {
        return new EmployeeUpdateRequest(
                id, "NewName", "NewLast", "new@example.com", "+905551110000",
                "Manager", "ECName", "+905553336655", roleIds, updatedById
        );
    }

    private Employee newEmployee(Long id) {
        Employee e = new Employee();
        e.setId(id);
        e.setFirstName("Test");
        e.setLastName("User");
        e.setEmail("test@example.com");
        e.setPhone("+905550000000");
        e.setHireDate(LocalDate.now());
        e.setCreatedAt(LocalDateTime.now());
        return e;
    }

    @Test
    void whenCreateEmployee_withValidRequest_thenReturnResponse() {
        Set<Long> roleIds = new HashSet<>(Arrays.asList(1L, 2L));
        EmployeeCreateRequest req = buildCreateReq(roleIds, null);
        Employee mapped = newEmployee(null);

        when(employeeMapper.toEntity(req)).thenReturn(mapped);
        when(encoder.encode(req.password())).thenReturn("ENCODED");
        when(roleRepository.findAllById(roleIds)).thenReturn(Arrays.asList(new Role(), new Role()));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(i -> {
            Employee e = i.getArgument(0);
            e.setId(10L);
            return e;
        });
        when(employeeMapper.toResponse(any(Employee.class))).thenReturn(
                new EmployeeResponse(10L, "John", "Doe", "john@example.com", "+905551112233", "Developer",
                        LocalDate.now(), null, null, "Jane Doe", "+905559998877", roleIds,
                        LocalDateTime.now(), null)
        );

        EmployeeResponse resp = employeeService.createEmployee(req);

        assertEquals(10L, resp.id());
        verify(roleRepository).findAllById(roleIds);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void whenCreateEmployee_withCreatedByEmployee_thenSetCreatedBy() {
        EmployeeCreateRequest req = buildCreateReq(Set.of(1L), 5L);
        Employee mapped = newEmployee(null);
        Employee creator = newEmployee(5L);

        when(employeeMapper.toEntity(req)).thenReturn(mapped);
        when(encoder.encode(req.password())).thenReturn("ENCODED");
        when(roleRepository.findAllById(any())).thenReturn(singletonList(new Role()));
        when(employeeRepository.findById(5L)).thenReturn(Optional.of(creator));
        when(employeeRepository.save(any(Employee.class))).thenReturn(mapped);
        when(employeeMapper.toResponse(any())).thenReturn(mock(EmployeeResponse.class));

        employeeService.createEmployee(req);

        assertEquals(creator, mapped.getCreatedBy());
    }

    @Test
    void whenCreateEmployee_createdByNotFound_thenThrowException() {
        EmployeeCreateRequest req = buildCreateReq(Set.of(1L), 99L);
        Employee mapped = newEmployee(null);

        when(employeeMapper.toEntity(req)).thenReturn(mapped);
        when(encoder.encode(req.password())).thenReturn("ENCODED");
        when(roleRepository.findAllById(any())).thenReturn(singletonList(new Role()));
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.createEmployee(req));
    }

    @Test
    void whenUpdateEmployee_withValidRequest_thenUpdateFields() {
        Employee existing = newEmployee(1L);
        EmployeeUpdateRequest req = buildUpdateReq(1L, Set.of(2L), null);
        List<Role> roles = Arrays.asList(new Role());

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(roleRepository.findAllById(Set.of(2L))).thenReturn(roles);
        when(employeeRepository.save(existing)).thenReturn(existing);
        when(employeeMapper.toResponse(existing)).thenReturn(mock(EmployeeResponse.class));

        employeeService.updateEmployee(req);

        assertEquals("NewName", existing.getFirstName());
        assertEquals("NewLast", existing.getLastName());
        assertEquals("new@example.com", existing.getEmail());
    }

    @Test
    void whenUpdateEmployee_clearRoles_thenEmptyRolesSet() {
        Employee existing = newEmployee(1L);
        EmployeeUpdateRequest req = buildUpdateReq(1L, Collections.emptySet(), null);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(employeeRepository.save(existing)).thenReturn(existing);
        when(employeeMapper.toResponse(existing)).thenReturn(mock(EmployeeResponse.class));

        employeeService.updateEmployee(req);

        assertTrue(existing.getRoles().isEmpty());
    }

    @Test
    void whenUpdateEmployee_withUpdatedBy_thenSetUpdatedBy() {
        Employee existing = newEmployee(1L);
        Employee updater = newEmployee(5L);
        EmployeeUpdateRequest req = buildUpdateReq(1L, null, 5L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(employeeRepository.findById(5L)).thenReturn(Optional.of(updater));
        when(employeeRepository.save(existing)).thenReturn(existing);
        when(employeeMapper.toResponse(existing)).thenReturn(mock(EmployeeResponse.class));

        employeeService.updateEmployee(req);

        assertEquals(updater, existing.getUpdatedBy());
    }

    @Test
    void whenUpdateEmployee_notFound_thenThrowException() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EmployeeNotFoundException.class, () ->
                employeeService.updateEmployee(buildUpdateReq(99L, null, null))
        );
    }

    @Test
    void whenGetEmployeeById_found_thenReturnResponse() {
        Employee e = newEmployee(1L);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(e));
        when(employeeMapper.toResponse(e)).thenReturn(mock(EmployeeResponse.class));

        EmployeeResponse resp = employeeService.getEmployeeById(1L);

        assertNotNull(resp);
    }

    @Test
    void whenGetEmployeeById_notFound_thenThrowException() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById(1L));
    }

    @Test
    void whenGetAllEmployees_thenReturnList() {
        when(employeeRepository.findAll()).thenReturn(singletonList(newEmployee(1L)));
        when(employeeMapper.toResponse(any())).thenReturn(mock(EmployeeResponse.class));

        List<EmployeeResponse> list = employeeService.getAllEmployees();

        assertEquals(1, list.size());
    }

    @Test
    void whenDeleteEmployee_found_thenSoftDeleteSaved() {
        Employee e = newEmployee(1L);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(e));

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(e);
        verify(employeeRepository, never()).delete(any(Employee.class));
    }

    @Test
    void whenDeleteEmployee_notFound_thenThrowException() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(1L));
    }

    @Test
    void whenFindByEmail_found_thenReturnEmployee() {
        Employee e = newEmployee(1L);
        when(employeeRepository.findByEmail("a@b.com")).thenReturn(Optional.of(e));

        Employee result = employeeService.findByEmail("a@b.com");

        assertEquals(e, result);
    }

    @Test
    void whenFindByEmail_notFound_thenThrowException() {
        when(employeeRepository.findByEmail("a@b.com")).thenReturn(Optional.empty());
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.findByEmail("a@b.com"));
    }

    @Test
    void whenChangePassword_passwordsMatch_thenEncodeAndSave() {
        Employee e = newEmployee(1L);
        ForgotPasswordRequest req = new ForgotPasswordRequest("mail@mail.com", "NewPass1", "NewPass1");

        when(employeeRepository.findByEmail(req.email())).thenReturn(Optional.of(e));
        when(encoder.encode("NewPass1")).thenReturn("ENCODED");

        employeeService.changePassword(req);

        assertEquals("ENCODED", e.getPassword());
        verify(employeeRepository).save(e);
    }

    @Test
    void whenGetAllEmployeesPageable_thenReturnPagedResponse() {
        Page<Employee> page = new PageImpl<>(singletonList(newEmployee(1L)));
        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(employeeMapper.toResponse(any())).thenReturn(mock(EmployeeResponse.class));

        Page<EmployeeResponse> result = employeeService.getAllEmployeesPageable(0, 5, "id", "asc");

        assertEquals(1, result.getContent().size());
    }
}