package com.seanchun.RestaurantApi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class Table {
    // Each Table will be its own unique document within the Tables Collection
    @Id
    private Integer id;

    @JsonProperty("tableNumber")
    private Integer tableNumber;

    @JsonProperty("ordersList")
    private List<Order> ordersList;

    public Table() {
        super();
    }

    public Table(Integer tableNumber, List<Order> ordersList) {
        this.id = tableNumber;
        this.tableNumber = tableNumber;
        this.ordersList = ordersList;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public List<Order> getOrdersList() {
        return ordersList;
    }

    public void setOrdersList(List<Order> ordersList) {
        this.ordersList = ordersList;
    }
}
