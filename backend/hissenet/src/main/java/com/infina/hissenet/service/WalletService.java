package com.infina.hissenet.service;

import com.infina.hissenet.entity.Wallet;
import com.infina.hissenet.repository.WalletRepository;
import com.infina.hissenet.utils.IGenericService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WalletService implements IGenericService<Wallet, Long> {

    private WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository){
        this.walletRepository=walletRepository;
    }


    @Override
    public Wallet save(Wallet entity) {
        return walletRepository.save(entity);
    }

    @Override
    public Wallet update(Wallet entity) {
        return walletRepository.save(entity);
    }

    @Override
    public void delete(Wallet entity) {
        entity.setDeleted(true);
        update(entity);
    }

    @Override
    public void deleteById(Long id) {
        walletRepository.deleteById(id);
    }

    @Override
    public Optional<Wallet> findById(Long id) {
        return walletRepository.findById(id);
    }

    @Override
    public List<Wallet> findAll() {
        return walletRepository.findAll();
    }
}
