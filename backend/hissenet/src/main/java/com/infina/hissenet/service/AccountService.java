package com.infina.hissenet.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.infina.hissenet.dto.request.AccountCreateRequest;
import com.infina.hissenet.dto.response.AccountResponse;
import com.infina.hissenet.entity.Account;
import com.infina.hissenet.entity.Employee;
import com.infina.hissenet.exception.AccountNotFoundException;
import com.infina.hissenet.exception.EmployeeNotFoundException;
import com.infina.hissenet.mapper.AccountMapper;
import com.infina.hissenet.repository.AccountRepository;
import com.infina.hissenet.repository.EmployeeRepository;
import com.infina.hissenet.utils.GenericServiceImpl;

@Service
public class AccountService extends GenericServiceImpl<Account, Long> {

	private final AccountRepository accountRepository;
	private final EmployeeRepository employeeRepository;
	private final AccountMapper accountMapper;

	public AccountService(AccountRepository accountRepository, EmployeeRepository employeeRepository,
			AccountMapper accountMapper) {
		super(accountRepository);
		this.accountRepository = accountRepository;
		this.employeeRepository = employeeRepository;
		this.accountMapper = accountMapper;
	}

	public AccountResponse createAccount(AccountCreateRequest request) {
		Employee employee = employeeRepository.findById(request.employeeId())
				.orElseThrow(() -> new EmployeeNotFoundException(request.employeeId()));

		Account account = accountMapper.toEntity(request);
		account.setEmployee(employee);

		Account saved = save(account);
		return accountMapper.toResponse(saved);
	}

	public AccountResponse getAccountById(Long id) {
		Account account = findById(id).orElseThrow(() -> new AccountNotFoundException(id));
		return accountMapper.toResponse(account);
	}

	public List<AccountResponse> getAllAccounts() {
		return findAll().stream().map(accountMapper::toResponse).toList();
	}

	public AccountResponse updateAccount(Long id, AccountCreateRequest request) {
		Account existing = findById(id).orElseThrow(() -> new AccountNotFoundException(id));

		if (request.username() != null) {
			existing.setUsername(request.username());
		}

		if (request.employeeId() != null
				&& (existing.getEmployee() == null || !request.employeeId().equals(existing.getEmployee().getId()))) {
			Employee employee = employeeRepository.findById(request.employeeId())
					.orElseThrow(() -> new EmployeeNotFoundException(request.employeeId()));
			existing.setEmployee(employee);
		}

		Account updated = update(existing);
		return accountMapper.toResponse(updated);
	}

	public void deleteAccount(Long id) {
		Account account = findById(id).orElseThrow(() -> new AccountNotFoundException(id));
		delete(account);
	}

}
