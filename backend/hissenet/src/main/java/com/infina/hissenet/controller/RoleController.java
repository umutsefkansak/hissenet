package com.infina.hissenet.controller;

import com.infina.hissenet.common.ApiResponse;
import com.infina.hissenet.controller.doc.RoleControllerDoc;
import com.infina.hissenet.dto.request.RoleCreateRequest;
import com.infina.hissenet.dto.request.RoleUpdateRequest;
import com.infina.hissenet.dto.response.RoleResponse;
import com.infina.hissenet.service.abstracts.IRoleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController implements RoleControllerDoc{

    private final IRoleService roleService;

    public RoleController(IRoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody RoleCreateRequest dto) {
        RoleResponse createdRole = roleService.createRole(dto);
        ApiResponse<RoleResponse> response = ApiResponse.ok("Role created successfully", createdRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleResponse> getRoleById(@PathVariable Long id) {
        return ApiResponse.ok("Role retrieved successfully", roleService.getRoleById(id));
    }

    @GetMapping("/name/{name}")
    public ApiResponse<RoleResponse> getRoleByName(@PathVariable String name) {
        return ApiResponse.ok("Role retrieved successfully", roleService.getRoleByName(name).get());
    }

    @GetMapping
    public ApiResponse<List<RoleResponse>> getAllRoles() {
        return ApiResponse.ok("All roles retrieved successfully", roleService.getAllRoles());
    }

    @GetMapping("/page")
    public ApiResponse<Page<RoleResponse>> getAllRolesPaged(Pageable pageable) {
        return ApiResponse.ok("Paged roles retrieved successfully", roleService.getAllRoles(pageable));
    }

    @GetMapping("/active")
    public ApiResponse<List<RoleResponse>> getActiveRoles() {
        return ApiResponse.ok("Active roles retrieved successfully", roleService.getActiveRoles());
    }

    @GetMapping("/inactive")
    public ApiResponse<List<RoleResponse>> getInactiveRoles() {
        return ApiResponse.ok("Inactive roles retrieved successfully", roleService.getInactiveRoles());
    }

    @GetMapping("/search")
    public ApiResponse<List<RoleResponse>> searchRoles(
            @RequestParam String name,
            @RequestParam(required = false) Boolean isActive) {

        List<RoleResponse> roles;
        if (isActive != null) {
            roles = roleService.searchRolesByNameAndStatus(name, isActive);
        } else {
            roles = roleService.searchRolesByName(name);
        }
        return ApiResponse.ok("Roles searched successfully", roles);
    }

    @PutMapping("/{id}")
    public ApiResponse<RoleResponse> updateRole(@PathVariable Long id,
                                                @Valid @RequestBody RoleUpdateRequest dto) {
        return ApiResponse.ok("Role updated successfully", roleService.updateRole(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.ok("Role deleted successfully");
    }

    @PatchMapping("/{id}/activate")
    public ApiResponse<Void> activateRole(@PathVariable Long id) {
        roleService.activateRole(id);
        return ApiResponse.ok("Role activated successfully");
    }

    @PatchMapping("/{id}/deactivate")
    public ApiResponse<Void> deactivateRole(@PathVariable Long id) {
        roleService.deactivateRole(id);
        return ApiResponse.ok("Role deactivated successfully");
    }

    @GetMapping("/{id}/exists")
    public ApiResponse<Boolean> existsById(@PathVariable Long id) {
        boolean exists = roleService.existsById(id);
        return ApiResponse.ok("Role existence checked successfully", exists);
    }

    @GetMapping("/name/{name}/exists")
    public ApiResponse<Boolean> existsByName(@PathVariable String name) {
        boolean exists = roleService.existsByName(name);
        return ApiResponse.ok("Role name existence checked successfully", exists);
    }
}