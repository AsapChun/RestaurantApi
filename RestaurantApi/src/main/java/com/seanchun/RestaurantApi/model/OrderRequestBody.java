package com.seanchun.RestaurantApi.model;

import java.util.List;

public class OrderRequestBody {

    private Integer tableNumber;
    private List<String> foodItems;

    public OrderRequestBody(Integer tableNumber, List<String> foodItems) {
        this.tableNumber = tableNumber;
        this.foodItems = foodItems;
    }

    // Implemented custom validator as @Valid and @NotEmpty annotations were not working
    public boolean validateRequestBody() {
        if (this.tableNumber == null || this.foodItems == null || this.foodItems.isEmpty()) {
            return false;
        }
        for (String foodItem : foodItems) {
            if (foodItem == null ) {
                return false;
            }
        }
        return true;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public List<String> getFoodItems() {
        return this.foodItems;
    }

    public void setFoodItems(List<String> foodItems) {
        this.foodItems = foodItems;
    }
}
