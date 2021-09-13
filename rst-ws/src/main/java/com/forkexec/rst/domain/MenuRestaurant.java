package com.forkexec.rst.domain;

public class MenuRestaurant {

    private String id;
    private String entree;
    private String plate;
    private String dessert;
    private int price;
    private int preparation;
    private int quantity_available;

    public MenuRestaurant(String id, String entree, String plate, String dessert, int price, int preparation, int quantity_available) {
        setId(id);
        setEntree(entree);
        setPlate(plate);
        setDessert(dessert);
        setPrice(price);
        setPreparation(preparation);
        setQuantity(quantity_available);
    }

    public void setId(String id) { this.id = id; }
    public String getId() { return this.id; }

    public void setEntree(String entree) { this.entree = entree; }
    public String getEntree() { return this.entree; }

    public void setPlate(String plate) { this.plate = plate; }
    public String getPlate() { return this.plate; }

    public void setDessert(String dessert) { this.dessert = dessert; }
    public String getDessert() {return this.dessert; }

    public void setPrice(int price) { this.price = price; }
    public int getPrice() { return this.price; }

    public void setPreparation(int preparation) { this.preparation = preparation; }
    public int getPreparation() { return this.preparation; }

    public void setQuantity(int quantity_available) { this.quantity_available = quantity_available; }
    public int getQuantity() { return this.quantity_available; }

}