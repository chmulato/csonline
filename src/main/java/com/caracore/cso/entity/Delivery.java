package com.caracore.cso.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "business", "customer", "courier", "smsList", "datatime"})
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "idbusiness")
    @JsonIgnore
    private User business;

    @ManyToOne
    @JoinColumn(name = "idcustomer")
    @JsonIgnore
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "idcourier")
    @JsonIgnore
    private Courier courier;

    private String start;
    private String destination;
    private String contact;
    private String description;
    private String volume;
    private String weight;
    private String km;
    private Double additionalCost;
    private Double cost;
    private Boolean received;
    private Boolean completed;
    
    @JsonIgnore
    private LocalDateTime datatime;

    @OneToMany(mappedBy = "delivery")
    @JsonIgnore
    private java.util.List<SMS> smsList;

    // Exposing IDs for JSON serialization
    @JsonProperty("businessId")
    public Long getBusinessId() {
        return business != null ? business.getId() : null;
    }

    @JsonProperty("businessId")
    public void setBusinessId(Long businessId) {
        // This is handled by the controller
    }

    @JsonProperty("customerId")
    public Long getCustomerId() {
        return customer != null ? customer.getId() : null;
    }

    @JsonProperty("customerId")
    public void setCustomerId(Long customerId) {
        // This is handled by the controller
    }

    @JsonProperty("courierId")
    public Long getCourierId() {
        return courier != null ? courier.getId() : null;
    }

    @JsonProperty("courierId")
    public void setCourierId(Long courierId) {
        // This is handled by the controller
    }

    public java.util.List<SMS> getSmsList() { return smsList; }
    public void setSmsList(java.util.List<SMS> smsList) { this.smsList = smsList; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getBusiness() { return business; }
    public void setBusiness(User business) { this.business = business; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Courier getCourier() { return courier; }
    public void setCourier(Courier courier) { this.courier = courier; }

    public String getStart() { return start; }
    public void setStart(String start) { this.start = start; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getVolume() { return volume; }
    public void setVolume(String volume) { this.volume = volume; }

    public String getWeight() { return weight; }
    public void setWeight(String weight) { this.weight = weight; }

    public String getKm() { return km; }
    public void setKm(String km) { this.km = km; }

    public Double getAdditionalCost() { return additionalCost; }
    public void setAdditionalCost(Double additionalCost) { this.additionalCost = additionalCost; }

    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }

    public Boolean getReceived() { return received; }
    public void setReceived(Boolean received) { this.received = received; }

    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }

    public java.time.LocalDateTime getDatatime() { return datatime; }
    public void setDatatime(java.time.LocalDateTime datatime) { this.datatime = datatime; }
}
