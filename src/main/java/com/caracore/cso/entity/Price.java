package com.caracore.cso.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "price")
public class Price {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idbusiness")
    private User business;

    @ManyToOne
    @JoinColumn(name = "idcustomer")
    private Customer customer;

    private String tableName;
    private String vehicle;
    private String local;
    private Double price;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getBusiness() { return business; }
    public void setBusiness(User business) { this.business = business; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public String getVehicle() { return vehicle; }
    public void setVehicle(String vehicle) { this.vehicle = vehicle; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
