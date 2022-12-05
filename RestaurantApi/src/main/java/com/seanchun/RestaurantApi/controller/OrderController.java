package com.seanchun.RestaurantApi.controller;

import com.seanchun.RestaurantApi.model.OrderRequestBody;
import com.seanchun.RestaurantApi.model.Table;
import com.seanchun.RestaurantApi.model.DeleteOrderRequestBody;
import com.seanchun.RestaurantApi.services.OrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("orders")
@Slf4j
public class OrderController {

    @Autowired
    private OrderServiceImpl orderService;

    @GetMapping("/getAllOrders")
    @ResponseBody
    public ResponseEntity getAllOrders() {
        log.info("Get All Orders For All Tables");
        try {
            List<Table> allOrders = orderService.getAllOrders();
            if (allOrders == null || allOrders.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Pending Orders");
            } else {
                return new ResponseEntity<>(allOrders, HttpStatus.FOUND);
            }
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex);
        }
    }

    /*
        The application MUST, upon query request, show all items for a specified table number.

        The client MAY limit the number of specific tables in its requests to a finite set (at least 100).
        --> Assuming this means a User will be able to make query request with up to 100 tables?

        Example Request URL: http://localhost:8080/orders/getOrdersForTable?tableList=1,2,3,4,5
     */
    @GetMapping("/getOrdersForTable")
    @ResponseBody
    public ResponseEntity getOrdersForTable(@RequestParam List<Integer> tableList) {
        if (tableList == null || tableList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid TableList Input");
        }
        if (tableList.size() > 100) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parameter Error: max tableList request size 100");
        }
        try {
            log.info("Requested # of Tables: " + tableList.size());
            List<Table> tables = orderService.getOrdersForTable(tableList);
            if (tables == null) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(tables, HttpStatus.FOUND);
            }
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex);
        }
    }

    /*
        The application MUST, upon query request, show a specified item for a specified table number.

        Example Request URL: http://localhost:8080/orders/getItemForTable?tableNumber=2&item=Ramen
     */
    @GetMapping("/getItemForTable")
    @ResponseBody
    public ResponseEntity getItemForTable(@RequestParam Integer tableNumber, @RequestParam String item) {
        if (tableNumber == null || tableNumber < 0 || item.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Request Parameter");
        }
        log.info("Get Item: " + item + "; from Table #: " + tableNumber);
        try {
            Table table = orderService.getOrderForItemAndTable(tableNumber, item);
            if (table == null || table.getOrdersList().isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(table, HttpStatus.FOUND);
            }
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex);
        }
    }

    /*
        The client (the restaurant staff “devices” making the requests) MUST be able to:
        add one or more items with a table number
     */
    @PostMapping(path = "/createOrder",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createOrder(@RequestBody OrderRequestBody orderRequest) {
        log.info("New Order Received from Table #: " + orderRequest.getTableNumber());
        if (!orderRequest.validateRequestBody()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Request Body");
        }
        try {
            Table table = orderService.addNewOrder(orderRequest);
            if (table == null) {
                log.error("Unable to create new Table");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to create Table");
            } else {
                log.info("Order SuccessFully Updated for Table #: " + orderRequest.getTableNumber());
                log.info("Updated itemOrder List: " + table.getOrdersList().size());
                return new ResponseEntity<>(table, HttpStatus.CREATED);
            }
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex);
        }
    }

    /*
        The client (the restaurant staff “devices” making the requests) MUST be able to:
        remove an item for a table

        *** Client should first use GET endpoints to get necessary Order UID parameter***
     */
    @PostMapping(path = "/deleteOrder",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteOrder(@RequestBody DeleteOrderRequestBody deleteOrder) {
        log.info("Delete Order Received for Table #: " + deleteOrder.getTableNumber());
        if (!deleteOrder.validateRequestBody()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Request Body");
        }
        try {
            Table table = orderService.removeItemForTable(deleteOrder.getTableNumber(), deleteOrder.getUID());
            if (table == null) {
                log.error("Invalid Table or Item UID");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid Table or Item UID");
            } else {
                log.info("Order ID- " + deleteOrder.getUID() +" successFully removed from Table #: " + deleteOrder.getTableNumber());
                return new ResponseEntity<>(table, HttpStatus.ACCEPTED);
            }
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex);
        }
    }
}

