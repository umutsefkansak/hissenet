package com.infina.hissenet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infina.hissenet.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{

}
