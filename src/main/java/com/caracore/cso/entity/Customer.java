package com.caracore.cso.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "customer")
public class Customer {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idbusiness", referencedColumnName = "id")
    private User business;

    @OneToOne
    @JoinColumn(name = "idcustomer", referencedColumnName = "id")
    private User user;

    private Double factorCustomer;
    private String priceTable;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public User getBusiness() {
        return business;
    }
    public void setBusiness(User business) {
        this.business = business;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Double getFactorCustomer() {
        return factorCustomer;
    }
    public void setFactorCustomer(Double factorCustomer) {
        this.factorCustomer = factorCustomer;
    }

    public String getPriceTable() {
        return priceTable;
    }
    public void setPriceTable(String priceTable) {
        this.priceTable = priceTable;
    }
}
