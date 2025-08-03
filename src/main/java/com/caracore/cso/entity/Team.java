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
        return business != null ? business.getId() : null;
    }

    @JsonProperty("courierId")
    public Long getCourierId() {
        return courier != null ? courier.getId() : null;
    }
    
    // Setters for JSON deserialization
    @JsonProperty("businessId")
    public void setBusinessId(Long businessId) {
        // Será gerenciado pelo controller/service
    }
    
    @JsonProperty("courierId")
    public void setCourierId(Long courierId) {
        // Será gerenciado pelo controller/service
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getBusiness() { return business; }
    public void setBusiness(User business) { this.business = business; }

    public Courier getCourier() { return courier; }
    public void setCourier(Courier courier) { this.courier = courier; }

    public Double getFactorCourier() { return factorCourier; }
    public void setFactorCourier(Double factorCourier) { this.factorCourier = factorCourier; }
}
