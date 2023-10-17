package com.example.Entities;

import java.util.List;

public interface ICart {
    public void addProduct(Product product);

    public void removeProduct(Product product);

    public void emptyCart();

    public List<Product> getProducts();

    public User getUser();
}
