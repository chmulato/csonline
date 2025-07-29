package com.caracore.cso.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "idbusiness", referencedColumnName = "id")
    private User business;

    @ManyToOne
    @JoinColumn(name = "idcourier", referencedColumnName = "id")
    private User courier;

    @Column(name = "factor_courier")
    private Double factorCourier;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getBusiness() { return business; }
    public void setBusiness(User business) { this.business = business; }

    public User getCourier() { return courier; }
    public void setCourier(User courier) { this.courier = courier; }

    public Double getFactorCourier() { return factorCourier; }
    public void setFactorCourier(Double factorCourier) { this.factorCourier = factorCourier; }
}
