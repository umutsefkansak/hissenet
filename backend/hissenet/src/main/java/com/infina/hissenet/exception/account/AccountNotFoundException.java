package com.infina.hissenet.exception.account;

import com.infina.hissenet.exception.NotFoundException;

public class AccountNotFoundException extends NotFoundException {
	public AccountNotFoundException(Long id) {
        super("Account not found with id: " + id);
    }
}
