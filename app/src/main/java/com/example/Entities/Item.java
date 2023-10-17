package com.example.Entities;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.List;

public abstract class Item implements IItem, Serializable {

    private String name, id, variant, description;
    private Double price;
    private Integer stockQty;
    private List<String> imageURI;
    private List<String> details;

    public Item() { }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getVariant() {
        return variant;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return stockQty;
    }

    public List<String> getImageURI() {
        return imageURI;
    }

    public List<String> getDetails() {
        return details;
    }
}
