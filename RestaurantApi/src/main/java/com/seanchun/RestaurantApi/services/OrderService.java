package com.seanchun.RestaurantApi.services;

import com.seanchun.RestaurantApi.model.OrderRequestBody;
import com.seanchun.RestaurantApi.model.Table;

import java.util.List;

public interface OrderService {

    List<Table> getAllOrders();

    /*
        The application MUST, upon query request, show all items for a specified table number.
     */
    List<Table> getOrdersForTable(List<Integer> tableNumber);

    /*
        The application MUST, upon query request, show a specified item for a specified table number.
    */
    Table getOrderForItemAndTable(int tableNumber, String item);

    /*
        The application MUST, upon creation request, store the item, the table number,
        and how long the item will take to cook.
     */
    Table addNewOrder(OrderRequestBody newOrder);

    /*
        The application MUST, upon deletion request, remove a specified item for a specified table number.
    */
    Table removeItemForTable(int tableNumber, String orderUid);

}
