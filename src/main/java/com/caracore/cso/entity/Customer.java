package com.caracore.cso.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "idbusiness", referencedColumnName = "id")
    @JsonIgnore
    private User business;

    @OneToOne
    @JoinColumn(name = "iduser", referencedColumnName = "id")
    @JsonIgnore
    private User user;

    private Double factorCustomer;
    private String priceTable;

    // Exposing IDs for JSON serialization
    @JsonProperty("businessId")
    public Long getBusinessId() {
        return business != null ? business.getId() : null;
    }

    @JsonProperty("businessId")
    public void setBusinessId(Long businessId) {
        // This is handled by the controller
    }

    @JsonProperty("userId")
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    @JsonProperty("userId")
    public void setUserId(Long userId) {
        // This is handled by the controller
    }

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
