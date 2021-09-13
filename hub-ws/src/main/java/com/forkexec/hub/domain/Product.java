package com.forkexec.hub.domain;

public class Product {

    private String id;
    private String restId;
    private String entree;
    private String plate;
    private String dessert;
    private int price;
    private int preparationTime;
    private int quantity;

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public String getRestId() {
        return restId;
    }

    public void setRestId(String value) {
        this.restId = value;
    }

    public String getEntree() {
        return entree;
    }

    public void setEntree(String value) {
        this.entree = value;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String value) {
        this.plate = value;
    }

    public String getDessert() {
        return dessert;
    }

    public void setDessert(String value) {
        this.dessert = value;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int value) {
        this.price = value;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int value) {
        this.preparationTime = value;
    }

    public void setQuantity(int value) { this.quantity = value; }

    public int getQuantity() { return quantity; }
}
