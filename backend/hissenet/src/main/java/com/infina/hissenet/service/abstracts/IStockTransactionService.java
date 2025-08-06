package com.infina.hissenet.service.abstracts;


import com.infina.hissenet.entity.Order;
import com.infina.hissenet.entity.StockTransaction;


import java.util.List;

public interface IStockTransactionService {
    void saveAll(List<StockTransaction> stockTransactions);
    void createTransactionFromOrder(Order order);
    List<StockTransaction> findAll();
}
