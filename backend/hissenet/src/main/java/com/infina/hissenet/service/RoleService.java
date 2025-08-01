package com.infina.hissenet.service;


import com.infina.hissenet.dto.request.RoleCreateDto;
import com.infina.hissenet.dto.request.RoleUpdateDto;
import com.infina.hissenet.dto.response.RoleResponse;
import com.infina.hissenet.entity.Role;
import com.infina.hissenet.exception.role.RoleNotFoundException;
import com.infina.hissenet.exception.role.RoleAlreadyExistsException;
import com.infina.hissenet.mapper.RoleMapper;
import com.infina.hissenet.repository.RoleRepository;
import com.infina.hissenet.service.abstracts.IRoleService;
import com.infina.hissenet.utils.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoleService extends GenericServiceImpl<Role, Long> implements IRoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Autowired
    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        super(roleRepository);
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public RoleResponse createRole(RoleCreateDto createRoleDto) {
        // Role name benzersizlik kontrolü
        if (roleRepository.existsByName(createRoleDto.name())) {
            throw new RoleAlreadyExistsException(createRoleDto.name());
        }

        Role role = roleMapper.toEntity(createRoleDto);

        // isActive değeri null ise default olarak true yap
        if (role.getActive() == null) {
            role.setActive(true);
        }

        Role savedRole = save(role);
        return roleMapper.toDto(savedRole);
    }

    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        Role role = findById(id)
                .orElseThrow(() -> new RoleNotFoundException(id));
        return roleMapper.toDto(role);
    }

    @Transactional(readOnly = true)
    public Optional<RoleResponse> getRoleByName(String name) {
        return roleRepository.findByName(name)
                .map(roleMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return findAll()
                .stream()
                .map(roleMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<RoleResponse> getAllRoles(Pageable pageable) {
        return findAll(pageable)
                .map(roleMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getActiveRoles() {
        return roleRepository.findByIsActiveTrue()
                .stream()
                .map(roleMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getInactiveRoles() {
        return roleRepository.findByIsActiveFalse()
                .stream()
                .map(roleMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> searchRolesByName(String name) {
        return roleRepository.findByNameContaining(name)
                .stream()
                .map(roleMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> searchRolesByNameAndStatus(String name, Boolean isActive) {
        return roleRepository.findByIsActiveAndNameContaining(isActive, name)
                .stream()
                .map(roleMapper::toDto)
                .toList();
    }

    public RoleResponse updateRole(Long id, RoleUpdateDto updateRoleDto) {
        Role existingRole = findById(id)
                .orElseThrow(() -> new RoleNotFoundException(id));

        // Eğer name değiştiriliyorsa, yeni name'in başka bir role'da kullanılmadığını kontrol et
        if (updateRoleDto.name() != null &&
                !updateRoleDto.name().equals(existingRole.getName()) &&
                roleRepository.existsByName(updateRoleDto.name())) {
            throw new RoleAlreadyExistsException(updateRoleDto.name());
        }

        roleMapper.updateEntityFromDto(updateRoleDto, existingRole);

        Role updatedRole = update(existingRole);
        return roleMapper.toDto(updatedRole);
    }

    public void deleteRole(Long id) {
        Role role = findById(id)
                .orElseThrow(() -> new RoleNotFoundException(id));
        delete(role);
    }

    public void activateRole(Long id) {
        Role role = findById(id)
                .orElseThrow(() -> new RoleNotFoundException(id));
        role.setActive(true);
        update(role);
    }

    public void deactivateRole(Long id) {
        Role role = findById(id)
                .orElseThrow(() -> new RoleNotFoundException(id));
        role.setActive(false);
        update(role);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return roleRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }
}
