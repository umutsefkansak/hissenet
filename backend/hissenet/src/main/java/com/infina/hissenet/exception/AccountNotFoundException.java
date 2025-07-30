package com.infina.hissenet.exception;

public class AccountNotFoundException extends NotFoundException{
	public AccountNotFoundException(Long id) {
        super("Account not found with id: " + id);
    }
}
