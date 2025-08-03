package com.caracore.cso.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "courier")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "business", "user", "teams"})
public class Courier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "idbusiness", referencedColumnName = "id")
    @JsonIgnore
    private User business;

    @ManyToOne
    @JoinColumn(name = "idcourier", referencedColumnName = "id")
    @JsonIgnore
    private User user;

    private Double factorCourier;

    @OneToMany(mappedBy = "courier", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<Team> teams;

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

    public Double getFactorCourier() {
        return factorCourier;
    }
    public void setFactorCourier(Double factorCourier) {
        this.factorCourier = factorCourier;
    }

    public List<Team> getTeams() {
        return teams;
    }
    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
}
