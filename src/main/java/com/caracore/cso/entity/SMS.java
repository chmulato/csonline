package com.caracore.cso.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sms")
public class SMS {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "iddelivery")
    private Delivery delivery;

    private Integer piece;
    private String type;
    private String mobileTo;
    private String mobileFrom;
    private String message;
    private String datetime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Delivery getDelivery() { return delivery; }
    public void setDelivery(Delivery delivery) { this.delivery = delivery; }

    public Integer getPiece() { return piece; }
    public void setPiece(Integer piece) { this.piece = piece; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMobileTo() { return mobileTo; }
    public void setMobileTo(String mobileTo) { this.mobileTo = mobileTo; }

    public String getMobileFrom() { return mobileFrom; }
    public void setMobileFrom(String mobileFrom) { this.mobileFrom = mobileFrom; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDatetime() { return datetime; }
    public void setDatetime(String datetime) { this.datetime = datetime; }
}
