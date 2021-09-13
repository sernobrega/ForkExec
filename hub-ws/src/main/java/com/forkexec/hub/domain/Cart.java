package com.forkexec.hub.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Cart {

    private String cartId;

    private HashMap<Product, Integer> items;

    public Cart(String cartId) {
        this.cartId = cartId;
        items = new HashMap<>();
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getCartId() {
        return cartId;
    }

    public void addProduct(Product p, int q) {
        items.put(p, q);
    }

    public List<Product> getProducts() {
        return new ArrayList<>(items.keySet());
    }

    public void clearProducts() {
        items.clear();
    }

}
