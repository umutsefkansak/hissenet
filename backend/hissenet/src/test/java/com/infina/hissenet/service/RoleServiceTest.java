package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.RoleCreateRequest;
import com.infina.hissenet.dto.request.RoleUpdateRequest;
import com.infina.hissenet.dto.response.RoleResponse;
import com.infina.hissenet.entity.Role;
import com.infina.hissenet.exception.role.RoleNotFoundException;
import com.infina.hissenet.exception.role.RoleAlreadyExistsException;
import com.infina.hissenet.mapper.RoleMapper;
import com.infina.hissenet.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;

    private Role role;
    private RoleResponse roleResponse;
    private RoleCreateRequest roleCreateRequest;
    private RoleUpdateRequest roleUpdateRequest;

    @BeforeEach
    void setUp() {
        // Role entity setup
        role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
        role.setDescription("Administrator role");
        role.setActive(true);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());

        // Role response setup
        roleResponse = new RoleResponse(
                1L,
                "ADMIN",
                "Administrator role",
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                Collections.emptySet()
        );

        // Role create request setup
        roleCreateRequest = new RoleCreateRequest(
                "ADMIN",
                "Administrator role",
                true
        );

        // Role update request setup
        roleUpdateRequest = new RoleUpdateRequest(
                "SUPER_ADMIN",
                "Super Administrator role",
                true
        );
    }

    @Test
    void createRole_WhenRoleDoesNotExist_ShouldCreateSuccessfully() {
        // Given
        when(roleRepository.existsByName("ADMIN")).thenReturn(false);
        when(roleMapper.toEntity(roleCreateRequest)).thenReturn(role);
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(roleMapper.toDto(role)).thenReturn(roleResponse);

        // When
        RoleResponse result = roleService.createRole(roleCreateRequest);

        // Then
        assertNotNull(result);
        assertEquals("ADMIN", result.name());
        assertEquals("Administrator role", result.description());
        assertTrue(result.isActive());

        verify(roleRepository).existsByName("ADMIN");
        verify(roleRepository).save(role);
        verify(roleMapper).toEntity(roleCreateRequest);
        verify(roleMapper).toDto(role);
    }

    @Test
    void createRole_WhenRoleAlreadyExists_ShouldThrowException() {
        // Given
        when(roleRepository.existsByName("ADMIN")).thenReturn(true);

        // When & Then
        assertThrows(RoleAlreadyExistsException.class,
                () -> roleService.createRole(roleCreateRequest));

        verify(roleRepository).existsByName("ADMIN");
        verify(roleRepository, never()).save(any());
    }

    @Test
    void createRole_WhenActiveIsNull_ShouldSetActiveToTrue() {
        // Given
        RoleCreateRequest requestWithNullActive = new RoleCreateRequest(
                "USER", "User role", null
        );
        Role roleWithNullActive = new Role();
        roleWithNullActive.setName("USER");
        roleWithNullActive.setDescription("User role");
        roleWithNullActive.setActive(null);

        when(roleRepository.existsByName("USER")).thenReturn(false);
        when(roleMapper.toEntity(requestWithNullActive)).thenReturn(roleWithNullActive);
        when(roleRepository.save(any(Role.class))).thenReturn(roleWithNullActive);
        when(roleMapper.toDto(roleWithNullActive)).thenReturn(roleResponse);

        // When
        RoleResponse result = roleService.createRole(requestWithNullActive);

        // Then
        assertTrue(roleWithNullActive.getActive());
        verify(roleRepository).save(roleWithNullActive);
    }

    @Test
    void getRoleById_WhenRoleExists_ShouldReturnRole() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleMapper.toDto(role)).thenReturn(roleResponse);

        // When
        RoleResponse result = roleService.getRoleById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("ADMIN", result.name());

        verify(roleRepository).findById(1L);
        verify(roleMapper).toDto(role);
    }

    @Test
    void getRoleById_WhenRoleNotExists_ShouldThrowException() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RoleNotFoundException.class,
                () -> roleService.getRoleById(1L));

        verify(roleRepository).findById(1L);
        verify(roleMapper, never()).toDto(any());
    }

    @Test
    void getRoleByName_WhenRoleExists_ShouldReturnRole() {
        // Given
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));
        when(roleMapper.toDto(role)).thenReturn(roleResponse);

        // When
        Optional<RoleResponse> result = roleService.getRoleByName("ADMIN");

        // Then
        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().name());

        verify(roleRepository).findByName("ADMIN");
        verify(roleMapper).toDto(role);
    }

    @Test
    void getRoleByName_WhenRoleNotExists_ShouldThrowException() {
        // Given
        when(roleRepository.findByName("NONEXISTENT")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RoleNotFoundException.class,
                () -> roleService.getRoleByName("NONEXISTENT"));

        verify(roleRepository).findByName("NONEXISTENT");
        verify(roleMapper, never()).toDto(any());
    }

    @Test
    void getAllRoles_ShouldReturnAllRoles() {
        // Given
        Role userRole = new Role();
        userRole.setId(2L);
        userRole.setName("USER");

        RoleResponse userRoleResponse = new RoleResponse(
                2L, "USER", "User role", true,
                LocalDateTime.now(), LocalDateTime.now(), Collections.emptySet()
        );

        List<Role> roles = Arrays.asList(role, userRole);
        when(roleRepository.findAll()).thenReturn(roles);
        when(roleMapper.toDto(role)).thenReturn(roleResponse);
        when(roleMapper.toDto(userRole)).thenReturn(userRoleResponse);

        // When
        List<RoleResponse> result = roleService.getAllRoles();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.name().equals("ADMIN")));
        assertTrue(result.stream().anyMatch(r -> r.name().equals("USER")));

        verify(roleRepository).findAll();
    }

    @Test
    void getAllRolesWithPageable_ShouldReturnPagedRoles() {
        // Given
        List<Role> roles = Arrays.asList(role);
        Page<Role> rolePage = new PageImpl<>(roles);
        Pageable pageable = PageRequest.of(0, 10);

        when(roleRepository.findAll(pageable)).thenReturn(rolePage);
        when(roleMapper.toDto(role)).thenReturn(roleResponse);

        // When
        Page<RoleResponse> result = roleService.getAllRoles(pageable);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals("ADMIN", result.getContent().get(0).name());
        assertEquals(0, result.getNumber());
        assertEquals(1, result.getTotalElements());

        verify(roleRepository).findAll(pageable);
    }

    @Test
    void getActiveRoles_ShouldReturnOnlyActiveRoles() {
        // Given
        List<Role> activeRoles = Arrays.asList(role);
        when(roleRepository.findByIsActiveTrue()).thenReturn(activeRoles);
        when(roleMapper.toDto(role)).thenReturn(roleResponse);

        // When
        List<RoleResponse> result = roleService.getActiveRoles();

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());

        verify(roleRepository).findByIsActiveTrue();
    }

    @Test
    void getInactiveRoles_ShouldReturnOnlyInactiveRoles() {
        // Given
        Role inactiveRole = new Role();
        inactiveRole.setId(2L);
        inactiveRole.setName("INACTIVE_ROLE");
        inactiveRole.setActive(false);

        RoleResponse inactiveRoleResponse = new RoleResponse(
                2L, "INACTIVE_ROLE", "Inactive role", false,
                LocalDateTime.now(), LocalDateTime.now(), Collections.emptySet()
        );

        List<Role> inactiveRoles = Arrays.asList(inactiveRole);
        when(roleRepository.findByIsActiveFalse()).thenReturn(inactiveRoles);
        when(roleMapper.toDto(inactiveRole)).thenReturn(inactiveRoleResponse);

        // When
        List<RoleResponse> result = roleService.getInactiveRoles();

        // Then
        assertEquals(1, result.size());
        assertFalse(result.get(0).isActive());

        verify(roleRepository).findByIsActiveFalse();
    }

    @Test
    void searchRolesByName_ShouldReturnMatchingRoles() {
        // Given
        String searchTerm = "ADM";
        List<Role> matchingRoles = Arrays.asList(role);
        when(roleRepository.findByNameContaining(searchTerm)).thenReturn(matchingRoles);
        when(roleMapper.toDto(role)).thenReturn(roleResponse);

        // When
        List<RoleResponse> result = roleService.searchRolesByName(searchTerm);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).name().contains("ADM"));

        verify(roleRepository).findByNameContaining(searchTerm);
    }

    @Test
    void searchRolesByNameAndStatus_ShouldReturnMatchingRoles() {
        // Given
        String searchTerm = "ADM";
        Boolean isActive = true;
        List<Role> matchingRoles = Arrays.asList(role);
        when(roleRepository.findByIsActiveAndNameContaining(isActive, searchTerm))
                .thenReturn(matchingRoles);
        when(roleMapper.toDto(role)).thenReturn(roleResponse);

        // When
        List<RoleResponse> result = roleService.searchRolesByNameAndStatus(searchTerm, isActive);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).name().contains("ADM"));
        assertTrue(result.get(0).isActive());

        verify(roleRepository).findByIsActiveAndNameContaining(isActive, searchTerm);
    }

    @Test
    void updateRole_WhenValidUpdate_ShouldUpdateSuccessfully() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.existsByName("SUPER_ADMIN")).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(roleMapper.toDto(role)).thenReturn(roleResponse);

        // When
        RoleResponse result = roleService.updateRole(1L, roleUpdateRequest);

        // Then
        assertNotNull(result);
        verify(roleMapper).updateEntityFromDto(roleUpdateRequest, role);
        verify(roleRepository).save(role);
        verify(roleMapper).toDto(role);
    }

    @Test
    void updateRole_WhenRoleNotExists_ShouldThrowException() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RoleNotFoundException.class,
                () -> roleService.updateRole(1L, roleUpdateRequest));

        verify(roleRepository).findById(1L);
        verify(roleRepository, never()).save(any());
    }

    @Test
    void updateRole_WhenNewNameAlreadyExists_ShouldThrowException() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.existsByName("SUPER_ADMIN")).thenReturn(true);

        // When & Then
        assertThrows(RoleAlreadyExistsException.class,
                () -> roleService.updateRole(1L, roleUpdateRequest));

        verify(roleRepository).findById(1L);
        verify(roleRepository).existsByName("SUPER_ADMIN");
        verify(roleRepository, never()).save(any());
    }

    @Test
    void updateRole_WhenNameNotChanged_ShouldNotCheckExistence() {
        // Given
        RoleUpdateRequest sameNameRequest = new RoleUpdateRequest(
                "ADMIN", "Updated description", true
        );

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(roleMapper.toDto(role)).thenReturn(roleResponse);

        // When
        RoleResponse result = roleService.updateRole(1L, sameNameRequest);

        // Then
        assertNotNull(result);
        verify(roleRepository, never()).existsByName(anyString());
        verify(roleMapper).updateEntityFromDto(sameNameRequest, role);
        verify(roleRepository).save(role);
    }

    @Test
    void deleteRole_WhenRoleExists_ShouldDeleteSuccessfully() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        // When
        roleService.deleteRole(1L);

        // Then
        assertTrue(role.getDeleted()); // Soft delete check
        verify(roleRepository).findById(1L);
        verify(roleRepository).save(role);
    }

    @Test
    void deleteRole_WhenRoleNotExists_ShouldThrowException() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RoleNotFoundException.class,
                () -> roleService.deleteRole(1L));

        verify(roleRepository).findById(1L);
        verify(roleRepository, never()).save(any());
    }

    @Test
    void activateRole_WhenRoleExists_ShouldActivateSuccessfully() {
        // Given
        role.setActive(false);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        // When
        roleService.activateRole(1L);

        // Then
        assertTrue(role.getActive());
        verify(roleRepository).findById(1L);
        verify(roleRepository).save(role);
    }

    @Test
    void activateRole_WhenRoleNotExists_ShouldThrowException() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RoleNotFoundException.class,
                () -> roleService.activateRole(1L));

        verify(roleRepository).findById(1L);
        verify(roleRepository, never()).save(any());
    }

    @Test
    void deactivateRole_WhenRoleExists_ShouldDeactivateSuccessfully() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        // When
        roleService.deactivateRole(1L);

        // Then
        assertFalse(role.getActive());
        verify(roleRepository).findById(1L);
        verify(roleRepository).save(role);
    }

    @Test
    void deactivateRole_WhenRoleNotExists_ShouldThrowException() {
        // Given
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RoleNotFoundException.class,
                () -> roleService.deactivateRole(1L));

        verify(roleRepository).findById(1L);
        verify(roleRepository, never()).save(any());
    }

    @Test
    void existsById_WhenRoleExists_ShouldReturnTrue() {
        // Given
        when(roleRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = roleService.existsById(1L);

        // Then
        assertTrue(result);
        verify(roleRepository).existsById(1L);
    }

    @Test
    void existsById_WhenRoleNotExists_ShouldReturnFalse() {
        // Given
        when(roleRepository.existsById(1L)).thenReturn(false);

        // When
        boolean result = roleService.existsById(1L);

        // Then
        assertFalse(result);
        verify(roleRepository).existsById(1L);
    }

    @Test
    void existsByName_WhenRoleExists_ShouldReturnTrue() {
        // Given
        when(roleRepository.existsByName("ADMIN")).thenReturn(true);

        // When
        boolean result = roleService.existsByName("ADMIN");

        // Then
        assertTrue(result);
        verify(roleRepository).existsByName("ADMIN");
    }

    @Test
    void existsByName_WhenRoleNotExists_ShouldReturnFalse() {
        // Given
        when(roleRepository.existsByName("NONEXISTENT")).thenReturn(false);

        // When
        boolean result = roleService.existsByName("NONEXISTENT");

        // Then
        assertFalse(result);
        verify(roleRepository).existsByName("NONEXISTENT");
    }
}