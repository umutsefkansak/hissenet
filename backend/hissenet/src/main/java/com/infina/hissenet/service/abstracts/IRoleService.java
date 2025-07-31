package com.infina.hissenet.service.abstracts;

import com.infina.hissenet.dto.request.RoleCreateDto;
import com.infina.hissenet.dto.request.RoleUpdateDto;
import com.infina.hissenet.dto.response.RoleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Role entity operations.
 * Provides business logic methods for role management including
 * creation, retrieval, update, deletion and status management operations.
 *
 * <p>This interface defines the contract for role-related business operations
 * such as creating roles with uniqueness validation, managing role activation status,
 * searching roles by various criteria, and performing CRUD operations on role entities.</p>
 *
 * @author Umut Sefkan SAK
 * @version 1.0
 * @since 1.0
 */
public interface IRoleService {

    /**
     * Creates a new role.
     * Validates role name uniqueness before creation.
     * If isActive is null, it will be set to true by default.
     *
     * @param createRoleDto the role creation data transfer object
     * @return the created role response
     * @throws com.infina.hissenet.exception.RoleAlreadyExistsException if role name already exists
     */
    RoleResponse createRole(RoleCreateDto createRoleDto);

    /**
     * Retrieves a role by its unique identifier.
     *
     * @param id the role identifier
     * @return the role response
     * @throws com.infina.hissenet.exception.RoleNotFoundException if the role does not exist
     */
    RoleResponse getRoleById(Long id);

    /**
     * Retrieves a role by its name.
     *
     * @param name the role name
     * @return optional containing the role response, empty if not found
     */
    Optional<RoleResponse> getRoleByName(String name);

    /**
     * Retrieves all roles in the system.
     *
     * @return list of all role responses
     */
    List<RoleResponse> getAllRoles();

    /**
     * Retrieves all roles with pagination support.
     *
     * @param pageable pagination information
     * @return paginated role responses
     */
    Page<RoleResponse> getAllRoles(Pageable pageable);

    /**
     * Retrieves all active roles.
     *
     * @return list of active role responses
     */
    List<RoleResponse> getActiveRoles();

    /**
     * Retrieves all inactive roles.
     *
     * @return list of inactive role responses
     */
    List<RoleResponse> getInactiveRoles();

    /**
     * Searches roles by name containing the specified text.
     * Case-sensitive search operation.
     *
     * @param name the text to search in role names
     * @return list of role responses containing the specified text
     */
    List<RoleResponse> searchRolesByName(String name);

    /**
     * Searches roles by name and activation status.
     * Combines name search with status filtering.
     *
     * @param name the text to search in role names
     * @param isActive the activation status to filter by
     * @return list of role responses matching both criteria
     */
    List<RoleResponse> searchRolesByNameAndStatus(String name, Boolean isActive);

    /**
     * Updates an existing role with new information.
     * Validates role name uniqueness before update (excluding current role).
     *
     * @param id the role identifier
     * @param updateRoleDto the role update data transfer object
     * @return the updated role response
     * @throws com.infina.hissenet.exception.RoleNotFoundException if the role does not exist
     * @throws com.infina.hissenet.exception.RoleAlreadyExistsException if new role name already exists
     */
    RoleResponse updateRole(Long id, RoleUpdateDto updateRoleDto);

    /**
     * Deletes a role by its unique identifier.
     *
     * @param id the role identifier
     * @throws com.infina.hissenet.exception.RoleNotFoundException if the role does not exist
     */
    void deleteRole(Long id);

    /**
     * Activates a role by setting its status to active.
     *
     * @param id the role identifier
     * @throws com.infina.hissenet.exception.RoleNotFoundException if the role does not exist
     */
    void activateRole(Long id);

    /**
     * Deactivates a role by setting its status to inactive.
     *
     * @param id the role identifier
     * @throws com.infina.hissenet.exception.RoleNotFoundException if the role does not exist
     */
    void deactivateRole(Long id);

    /**
     * Checks if a role exists by its unique identifier.
     *
     * @param id the role identifier
     * @return true if the role exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Checks if a role exists by its name.
     *
     * @param name the role name
     * @return true if a role with this name exists, false otherwise
     */
    boolean existsByName(String name);
}