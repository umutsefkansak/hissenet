package com.infina.hissenet.service.abstracts;

import java.util.List;

import com.infina.hissenet.dto.request.AccountCreateRequest;
import com.infina.hissenet.dto.request.AccountUpdateRequest;
import com.infina.hissenet.dto.response.AccountResponse;

/**
 * Service interface for account operations.
 * Handles creation, update, retrieval, listing, and deletion of accounts.
 */
public interface IAccountService {

    /**
     * Creates a new account.
     *
     * @param request account creation data
     * @return created account details
     */
    AccountResponse createAccount(AccountCreateRequest request);

    /**
     * Updates an existing account.
     *
     * @param id identifier of the account to update
     * @param request account update data
     * @return updated account details
     */
    AccountResponse updateAccount(Long id, AccountUpdateRequest request);

    /**
     * Retrieves an account by its ID.
     *
     * @param id account identifier
     * @return account details
     */
    AccountResponse getAccountById(Long id);

    /**
     * Lists all accounts.
     *
     * @return list of all accounts
     */
    List<AccountResponse> getAllAccounts();

    /**
     * Deletes an account by its ID.
     *
     * @param id account identifier to delete
     */
    void deleteAccount(Long id);

}
