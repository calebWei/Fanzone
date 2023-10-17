package com.example.Entities;

import java.util.List;

public class Cart implements ICart{

    private List<Product> products;
    private User user;

    public void addProduct(Product product) {
        products.add(product);
    }

    public void removeProduct(Product product) {
        products.remove(product);
    }

    public void emptyCart() {
        products.clear();
    }

    public List<Product> getProducts() {
        return products;
    }

    public User getUser() { return user; }

}
