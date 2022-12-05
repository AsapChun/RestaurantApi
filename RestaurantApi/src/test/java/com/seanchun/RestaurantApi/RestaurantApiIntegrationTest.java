package com.seanchun.RestaurantApi;

import com.seanchun.RestaurantApi.model.DeleteOrderRequestBody;
import com.seanchun.RestaurantApi.model.Order;
import com.seanchun.RestaurantApi.model.OrderRequestBody;
import com.seanchun.RestaurantApi.model.Table;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest(classes = RestaurantApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestaurantApiIntegrationTest {
    @LocalServerPort
    private int port;

    /*
        Ensure to run RestaurantApiApplication before running Integration Test
        “Clients” can be simulated as simple threads in a main() function calling the main server application
        with a variety of requests. There should be more than one, preferably around 5-10 running at any one time.
    */

    public static final String SERVER_URI = "http://localhost:8080/orders/";

    @Test
    public void multiThreadTest() throws InterruptedException {
        System.out.println("***** Multiple Concurrent Threads Test *****");
        int numberOfThreads = 10;

        /*
            Add New Orders
         */

        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        for (int i = 1; i <= 10; i++) {
            Integer tableNumber = i;
            service.execute(() -> {
                System.out.println("Thread #: " + tableNumber);
                testCreateOrders(tableNumber);
                latch.countDown();
            });
        }
        latch.await();
        RestTemplate restTemplate = new RestTemplate();
        List<LinkedHashMap> tableOrders = restTemplate.getForObject(SERVER_URI+"/getAllOrders", List.class);
        for (LinkedHashMap table : tableOrders) {
            List orders = (List) table.get("ordersList");
            Assert.assertEquals(orders.size(), 3);
        }

        /*
           Remove Added Orders
        */
        ExecutorService removeService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch removeLatch = new CountDownLatch(numberOfThreads);
        for (int i = 1; i <= 10; i++) {
            Integer tableNumber = i;
            removeService.execute(() -> {
                List<String> ordersToRemove = getOrderUID(tableNumber);
                for (String uid : ordersToRemove) {
                    testRemoveOrders(tableNumber, uid);
                }
                removeLatch.countDown();
            });
        }
        removeLatch.await();
        RestTemplate restTemplate2 = new RestTemplate();
        List<LinkedHashMap> tableOrders2 = restTemplate2.getForObject(SERVER_URI+"/getAllOrders", List.class);
        for (LinkedHashMap table : tableOrders2) {
            List orders = (List) table.get("ordersList");
            Assert.assertEquals(orders.size(), 0);
        }
    }

    private void testCreateOrders(Integer tableNumber) {
        RestTemplate restTemplate = new RestTemplate();
        System.out.println("Create Order for Table #: " + tableNumber);
        OrderRequestBody order = new OrderRequestBody(tableNumber,
                new ArrayList<>(Arrays.asList("Milk", "Curry", "Udon")));
        restTemplate.postForObject(SERVER_URI+"/createOrder", order, Table.class);
    }

    private List<String> getOrderUID(Integer tableNumber) {
        List<String> uidList = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        Table milkOrder = restTemplate.getForObject(SERVER_URI+"/getItemForTable?tableNumber="+tableNumber+"&item=Milk", Table.class);
        for (Order order : milkOrder.getOrdersList()) {
            uidList.add(order.getUID());
        }
        Table curryOrder = restTemplate.getForObject(SERVER_URI+"/getItemForTable?tableNumber="+tableNumber+"&item=Curry", Table.class);
        for (Order order : curryOrder.getOrdersList()) {
            uidList.add(order.getUID());
        }
        Table udonOrder = restTemplate.getForObject(SERVER_URI+"/getItemForTable?tableNumber="+tableNumber+"&item=Udon", Table.class);
        for (Order order : udonOrder.getOrdersList()) {
            uidList.add(order.getUID());
        }
        return uidList;
    }

    private void testRemoveOrders(Integer tableNumber, String uid) {
        RestTemplate restTemplate = new RestTemplate();
        DeleteOrderRequestBody deleteOrderRequestBody = new DeleteOrderRequestBody(tableNumber, uid);
        restTemplate.postForObject(SERVER_URI+"/deleteOrder", deleteOrderRequestBody, Table.class);
    }
}
