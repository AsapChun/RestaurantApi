package com.seanchun.RestaurantApi.model;

public class DeleteOrderRequestBody {
    private Integer tableNumber;
    private String UID;

    public DeleteOrderRequestBody(Integer tableNumber, String UID) {
        this.tableNumber = tableNumber;
        this.UID = UID;
    }

    // Implemented custom validator as @Valid and @NotEmpty annotations were not working
    public boolean validateRequestBody() {
        if (this.tableNumber == null || this.UID == null || this.UID.isBlank()) {
            return false;
        }
        return true;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
