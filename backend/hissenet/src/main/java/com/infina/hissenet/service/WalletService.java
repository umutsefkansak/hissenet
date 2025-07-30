package com.infina.hissenet.service;

import com.infina.hissenet.repository.WalletRepository;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    private WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository){
        this.walletRepository=walletRepository;
    }




}
