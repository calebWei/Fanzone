package com.example.Entities;

import java.util.List;

public interface IUser {

    public String getUserID();

    public String getUsername();

    public String getPassword();
    public String getUserEmail();

    public String getCartID();

    public List<Order> getOrders();

    public Cart getCart();
}
