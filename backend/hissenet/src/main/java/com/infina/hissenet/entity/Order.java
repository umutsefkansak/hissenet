package com.infina.hissenet.entity;

import java.math.BigDecimal;
import java.util.List;

import com.infina.hissenet.entity.base.BaseEntity;
import com.infina.hissenet.entity.enums.OrderCategory;
import com.infina.hissenet.entity.enums.OrderStatus;
import com.infina.hissenet.entity.enums.OrderType;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "orders", indexes = @Index(name = "idx_order_status", columnList = "order_status"))
@SQLRestriction("is_deleted = false")
public class Order extends BaseEntity{

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@Column(name = "order_category", nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderCategory category;

	@Column(name = "order_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderType type;
	
	@Column(name = "order_status", nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus status; 
	
	@Column(name = "stock_code", nullable = false, length = 20)
	private String stockCode;
	
	@Column(name = "quantity", nullable = false, precision = 20, scale = 4)
	private BigDecimal quantity;

	@Column(name = "price", precision = 20, scale = 4)
	private BigDecimal price;

	@Column(name = "total_amount", precision = 20, scale = 4)
	private BigDecimal totalAmount;

	@OneToMany(mappedBy = "order",cascade = CascadeType.ALL, orphanRemoval = true)
	List<StockTransaction> stockTransactionList;

	public List<StockTransaction> getStockTransactionList() {
		return stockTransactionList;
	}

	public void setStockTransactionList(List<StockTransaction> stockTransactionList) {
		this.stockTransactionList = stockTransactionList;
	}

	public Order() {
	}

	public Order(Customer customer, String stockCode, OrderCategory category, OrderType type, OrderStatus status,
			BigDecimal quantity, BigDecimal price, BigDecimal totalAmount) {
		super();
		this.customer = customer;
		this.stockCode = stockCode;
		this.category = category;
		this.type = type;
		this.status = status;
		this.quantity = quantity;
		this.price = price;
		this.totalAmount = totalAmount;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	
	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public OrderCategory getCategory() {
		return category;
	}

	public void setCategory(OrderCategory category) {
		this.category = category;
	}

	public OrderType getType() {
		return type;
	}

	public void setType(OrderType type) {
		this.type = type;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Override
	public String toString() {
	    return "Order{" +
	            "id=" + getId() +
	            ", customer=" + (customer != null ? customer.getId() : null) +
	            ", stockCode=" + stockCode +
	            ", category=" + category +
	            ", type=" + type +
	            ", status=" + status +
	            ", quantity=" + quantity +
	            ", price=" + price +
	            ", totalAmount=" + totalAmount +
	            ", createdAt=" + getCreatedAt() +
	            ", updatedAt=" + getUpdatedAt() +
	            ", createdBy=" + (getCreatedBy() != null ? getCreatedBy().getId() : null) +
	            ", updatedBy=" + (getUpdatedBy() != null ? getUpdatedBy().getId() : null) +
	            '}';
	}
	
}
