package com.seanchun.RestaurantApi.services;

import com.mongodb.MongoException;
import com.mongodb.client.result.UpdateResult;
import com.seanchun.RestaurantApi.model.Order;
import com.seanchun.RestaurantApi.model.OrderRequestBody;
import com.seanchun.RestaurantApi.model.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Repository
@Slf4j
public class OrderServiceImpl implements OrderService{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Table> getAllOrders() {
        log.info("Order Service: getAllOrders");
        try {
            return mongoTemplate.findAll(Table.class, "tables");
        } catch (MongoException exception) {
            log.error(exception.toString());
            throw exception;
        }
    }

    @Override
    public List<Table> getOrdersForTable(List<Integer> tableNumbers) {
        log.info("Order Service: getOrdersForTable");
        Query query = new Query();
        query.addCriteria(Criteria.where("tableNumber").in(tableNumbers));
        try {
            List<Table> tables = mongoTemplate.find(query, Table.class, "tables");
            if (!tables.isEmpty()) {
                return tables;
            }
            else {
                return null;
            }
        } catch (MongoException exception) {
            log.error(exception.toString());
            throw exception;
        }
    }

    @Override
    public Table getOrderForItemAndTable(int tableNumber, String item) {
        log.info("Order Service: getOrderForItemAndTable");
        Query tableNumberQuery = new Query();
        tableNumberQuery.addCriteria(Criteria.where("tableNumber").is(tableNumber));
        try {
            List<Table> tableOrders = mongoTemplate.find(tableNumberQuery, Table.class, "tables");
            if (!tableOrders.isEmpty()) {
                // find all corresponding orders in the orders list
                List<Order> orderList = new ArrayList<>();
                for (Order order : tableOrders.get(0).getOrdersList()) {
                    if (order.getFoodItem().equals(item)) {
                        orderList.add(order);
                    }
                }
                return new Table(tableNumber, orderList);
            } else {
                return null;
            }
        } catch (MongoException exception) {
            log.error(exception.toString());
            throw exception;
        }
    }

    @Override
    public Table addNewOrder(OrderRequestBody newOrder) {
        log.info("Order Service: addNewOrder");
        try {
            // Check if collection contains any orders
            Query query = new Query();
            query.addCriteria(Criteria.where("tableNumber").is(newOrder.getTableNumber()));
            Table table = mongoTemplate.findOne(query, Table.class, "tables");
            if (table == null) {
                // Initialize Table in MongoDb
                log.info("Table Not Found");
                List<Order> ordersList = new ArrayList<>();
                table = new Table(newOrder.getTableNumber(), ordersList);
                mongoTemplate.save(table, "tables");
            }
            log.info("Updating Table #" + table.getTableNumber());
            for (String foodItem : newOrder.getFoodItems()) {
                Order initOrder = new Order(foodItem);
                table.getOrdersList().add(initOrder);
            }
            Update update = new Update();
            update.set("ordersList", table.getOrdersList());
            UpdateResult updateResult = mongoTemplate.updateFirst(query, update, "tables");
            log.info(updateResult.toString());
            return table;
        } catch (MongoException ex) {
            log.error(ex.toString());
            throw ex;
        }
    }

    @Override
    public Table removeItemForTable(int tableNumber, String orderUid) {
        log.info("Order Service: removeItemForTable");
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("tableNumber").is(tableNumber));
            Table table = mongoTemplate.findOne(query, Table.class, "tables");
            if (table != null) {
                for (Iterator<Order> iter = table.getOrdersList().listIterator(); iter.hasNext();) {
                    Order order = iter.next();
                    if (order.getUID().equals(orderUid)) {
                        iter.remove();
                        break;
                    }
                }
                Update update = new Update();
                update.set("ordersList", table.getOrdersList());
                mongoTemplate.updateFirst(query, update, "tables");
                return table;
            } else {
                return null;
            }
        } catch (MongoException ex) {
            log.error(ex.toString());
            throw ex;
        }
    }
}
