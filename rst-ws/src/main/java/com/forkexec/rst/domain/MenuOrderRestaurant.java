package com.forkexec.rst.domain;

public class MenuOrderRestaurant {

    private int menuOrderId;
    private String menuId;
    private int quantity;


    public MenuOrderRestaurant(int menuOrderId, String menuId, int quantity) {
        setMenuOrderId(menuOrderId);
        setMenuId(menuId);
        setQuantity(quantity);
    }

    public void setMenuOrderId(int menuOrderId) { this.menuOrderId = menuOrderId; }
    public int getMenuOrderId() { return this.menuOrderId; }

    public void setMenuId(String menuId) { this.menuId = menuId; }
    public String getMenuId() { return this.menuId; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getQuantity() { return this.quantity; }
}