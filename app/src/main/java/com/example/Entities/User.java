package com.example.Entities;

import java.util.List;

public class User implements IUser {

    private String username;
    private String password;
    private String email;
    private String userID;
    private String cartID;
    private Cart cart;
    private List<Order> pastOrders;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    public String getUserEmail() {return email;}

    public String getUserID() {
        return userID;
    }

    public String getCartID() {
        return cartID;
    }

    public List<Order> getOrders() {
        return pastOrders;
    }

    public Cart getCart() {
        return cart;
    }


}
