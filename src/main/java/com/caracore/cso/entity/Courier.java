package com.caracore.cso.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "courier")
public class Courier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "idbusiness", referencedColumnName = "id")
    @JsonBackReference("user-couriers")
    private User business;

    @ManyToOne
    @JoinColumn(name = "idcourier", referencedColumnName = "id")
    private User user;

    private Double factorCourier;

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

    public Double getFactorCourier() {
        return factorCourier;
    }
    public void setFactorCourier(Double factorCourier) {
        this.factorCourier = factorCourier;
    }
}
