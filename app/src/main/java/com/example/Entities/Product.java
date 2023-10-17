package com.example.Entities;

// A product is an item with a quantity
public class Product implements IProduct{

    Item item;
    Integer quantity;

    public Item getItem() {
        return item;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
