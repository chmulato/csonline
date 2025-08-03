package com.caracore.cso.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "idbusiness", referencedColumnName = "id")
    @JsonIgnore
    private User business;

    @ManyToOne
    @JoinColumn(name = "idcourier", referencedColumnName = "id")
    @JsonIgnore
    private Courier courier;

    @Column(name = "factorCourier")
    private Double factorCourier;

    // Exposing IDs for JSON serialization
    @JsonProperty("businessId")
    public Long getBusinessId() {
        if (business != null) {
            return business.getId();
        }
        return _businessId;
    }

    @JsonProperty("courierId")
    public Long getCourierId() {
        if (courier != null) {
            return courier.getId();
        }
        return _courierId;
    }
    
    // Setters for JSON deserialization
    @JsonProperty("businessId")
    public void setBusinessId(Long businessId) {
        // Armazena o ID para ser usado pelo controller/service
        this._businessId = businessId;
    }
    
    @JsonProperty("courierId")
    public void setCourierId(Long courierId) {
        // Armazena o ID para ser usado pelo controller/service
        this._courierId = courierId;
    }
    
    @Transient
    private Long _businessId;
    
    @Transient
    private Long _courierId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getBusiness() { return business; }
    public void setBusiness(User business) { this.business = business; }

    public Courier getCourier() { return courier; }
    public void setCourier(Courier courier) { this.courier = courier; }

    public Double getFactorCourier() { return factorCourier; }
    public void setFactorCourier(Double factorCourier) { this.factorCourier = factorCourier; }
}
