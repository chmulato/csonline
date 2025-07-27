package com.caracore.cso.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String password;

    private String email;
    private String email2;
    private String address;
    private String mobile;

    // Relationships
    @OneToMany(mappedBy = "business", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Customer> customers;

    @OneToMany(mappedBy = "business", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Courier> couriers;

    @OneToMany(mappedBy = "business", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Delivery> deliveries;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail2() {
        return email2;
    }
    public void setEmail2(String email2) {
        this.email2 = email2;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<Customer> getCustomers() {
        return customers;
    }
    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public List<Courier> getCouriers() {
        return couriers;
    }
    public void setCouriers(List<Courier> couriers) {
        this.couriers = couriers;
    }

    public List<Delivery> getDeliveries() {
        return deliveries;
    }
    public void setDeliveries(List<Delivery> deliveries) {
        this.deliveries = deliveries;
    }
}
