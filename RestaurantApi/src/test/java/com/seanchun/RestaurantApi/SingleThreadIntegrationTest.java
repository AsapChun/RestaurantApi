package com.seanchun.RestaurantApi;

import com.seanchun.RestaurantApi.model.DeleteOrderRequestBody;
import com.seanchun.RestaurantApi.model.Order;
import com.seanchun.RestaurantApi.model.OrderRequestBody;
import com.seanchun.RestaurantApi.model.Table;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

@SpringBootTest(classes = RestaurantApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SingleThreadIntegrationTest {

    public final String SERVER_URI = "http://localhost:8080/orders/";

    /*
        Ensure to run RestaurantApiApplication before running Integration Test
        Single Thread Integration Test to test basic functionality of all
        endpoints for a single thread
    */

    @Test
    public void singleThreadTest() throws InterruptedException {
        System.out.println("***** Single Thread Test *****");
        System.out.println("***** Create Order *****");
        testCreateOrders();

        System.out.println("***** GetOrdersForTable *****");
        List<LinkedHashMap> multiTables = testGetOrdersForTable();
        List tableOrders = (List) multiTables.get(0).get("ordersList");
        Assert.assertEquals(multiTables.size(), 3);
        Assert.assertEquals(tableOrders.size(), 1);

        System.out.println("***** GetItemForTable *****");
        Table table = testGetItemForTable();
        Assert.assertEquals(table.getTableNumber(), (Integer) 2);
        Assert.assertEquals(table.getOrdersList().size(), 1);

        System.out.println("***** RemoveOrders *****");
        testRemoveOrders();
        RestTemplate restTemplate = new RestTemplate();
        List<LinkedHashMap> deletedTables = restTemplate.getForObject(SERVER_URI+"/getOrdersForTable?tableList=1,2,3", List.class);
        Assert.assertEquals(deletedTables.size(), 3);
        List tOne = (List) deletedTables.get(0).get("ordersList");
        List tTwo = (List) deletedTables.get(1).get("ordersList");
        List tThree = (List) deletedTables.get(2).get("ordersList");
        Assert.assertEquals(tOne.size(), 0);
        Assert.assertEquals(tTwo.size(), 0);
        Assert.assertEquals(tThree.size(), 0);
    }

    private void testCreateOrders() {
        RestTemplate restTemplate = new RestTemplate();
        OrderRequestBody order1 = new OrderRequestBody(1,
                new ArrayList<>(Arrays.asList("Milk")));
        OrderRequestBody order2 = new OrderRequestBody(2,
                new ArrayList<>(Arrays.asList("Curry")));
        OrderRequestBody order3 = new OrderRequestBody(3,
                new ArrayList<>(Arrays.asList("Udon")));
        restTemplate.postForObject(SERVER_URI+"/createOrder", order1, Table.class);
        restTemplate.postForObject(SERVER_URI+"/createOrder", order2, Table.class);
        restTemplate.postForObject(SERVER_URI+"/createOrder", order3, Table.class);
    }

    private List<LinkedHashMap> testGetOrdersForTable() {
        RestTemplate restTemplate = new RestTemplate();
        //we can't get List<Table> because JSON convertor doesn't know the type of
        //object in the list and hence convert it to default JSON object type LinkedHashMap
        System.out.println("Get Orders for Tables: 1, 2, 3");
        return restTemplate.getForObject(SERVER_URI+"/getOrdersForTable?tableList=1,2,3", List.class);
    }

    private Table testGetItemForTable() {
        RestTemplate restTemplate = new RestTemplate();
        System.out.println("Get Curry Order from Table 2");
        return restTemplate.getForObject(SERVER_URI+"/getItemForTable?tableNumber=2&item=Curry", Table.class);
    }

    private void testRemoveOrders() {
        System.out.println("Removing Orders from Table 1,2,3");
        RestTemplate restTemplate = new RestTemplate();
        List<Table> tableList = new ArrayList<>();
        tableList.add(restTemplate.getForObject(SERVER_URI+"/getItemForTable?tableNumber=1&item=Milk", Table.class));
        tableList.add(restTemplate.getForObject(SERVER_URI+"/getItemForTable?tableNumber=2&item=Curry", Table.class));
        tableList.add(restTemplate.getForObject(SERVER_URI+"/getItemForTable?tableNumber=3&item=Udon", Table.class));

        for (Table table : tableList) {
            for (Order order: table.getOrdersList()) {
                DeleteOrderRequestBody deleteOrderRequestBody = new DeleteOrderRequestBody(table.getTableNumber(), order.getUID());
                restTemplate.postForObject(SERVER_URI+"/deleteOrder", deleteOrderRequestBody, Table.class);
            }
        }
    }

}
