package com.seanchun.RestaurantApi.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document
public class Order {

    private String UID;
    private int cookingTime;
    private String foodItem;

    public Order() {
        super();
    }

    public Order(String foodItem) {
        /*
            The application MAY assign a length of time for the item to prepare as
            a random time between 5-15 minutes.
        */
        this.UID = UUID.randomUUID().toString();
        this.cookingTime = (int) ((Math.random() * (15 - 5)) + 5);;
        this.foodItem = foodItem;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Order) {
            Order order = (Order) o;
            return this.UID.equals(order.getUID());
        } else {
            return false;
        }
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public String getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(String foodItem) {
        this.foodItem = foodItem;
    }

    // Overriding toString() method of String class
    @Override
    public String toString() {
        return "Order UID: " + this.UID + "; Cooking Time: " + this.cookingTime + "; foodItem: " + this.foodItem;
    }
}
