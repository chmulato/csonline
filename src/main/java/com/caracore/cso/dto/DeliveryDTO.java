package com.caracore.cso.dto;

public class DeliveryDTO {
    private Long id;
    private Long businessId;
    private Long customerId;
    private Long courierId;
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

    // Constructors
    public DeliveryDTO() {}

    public DeliveryDTO(Long id, Long businessId, Long customerId, Long courierId, 
                      String start, String destination, String contact, String description,
                      String volume, String weight, String km, Double additionalCost,
                      Double cost, Boolean received, Boolean completed) {
        this.id = id;
        this.businessId = businessId;
        this.customerId = customerId;
        this.courierId = courierId;
        this.start = start;
        this.destination = destination;
        this.contact = contact;
        this.description = description;
        this.volume = volume;
        this.weight = weight;
        this.km = km;
        this.additionalCost = additionalCost;
        this.cost = cost;
        this.received = received;
        this.completed = completed;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBusinessId() { return businessId; }
    public void setBusinessId(Long businessId) { this.businessId = businessId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getCourierId() { return courierId; }
    public void setCourierId(Long courierId) { this.courierId = courierId; }

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
}
