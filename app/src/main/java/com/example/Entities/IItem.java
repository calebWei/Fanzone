package com.example.Entities;

import java.util.List;

public interface IItem {

    public Double getPrice();
    public String getName();
    public String getId();
    public String getVariant();
    public String getDescription();
    public Integer getQuantity();
    public List<String> getImageURI();
    public List<String> getDetails();

}
