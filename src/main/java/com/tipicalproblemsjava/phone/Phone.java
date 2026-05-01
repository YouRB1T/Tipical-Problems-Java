package com.tipicalproblemsjava.phone;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "phones")
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private Integer storageGb;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private boolean inStock;

    protected Phone() {
    }

    public Phone(String brand, String model, Integer storageGb, Integer price, boolean inStock) {
        this.brand = brand;
        this.model = model;
        this.storageGb = storageGb;
        this.price = price;
        this.inStock = inStock;
    }

    public Long getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public Integer getStorageGb() {
        return storageGb;
    }

    public Integer getPrice() {
        return price;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void update(String brand, String model, Integer storageGb, Integer price, boolean inStock) {
        this.brand = brand;
        this.model = model;
        this.storageGb = storageGb;
        this.price = price;
        this.inStock = inStock;
    }
}
