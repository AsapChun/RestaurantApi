package com.seanchun.RestaurantApi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;
import com.seanchun.RestaurantApi.controller.OrderController;
import com.seanchun.RestaurantApi.model.DeleteOrderRequestBody;
import com.seanchun.RestaurantApi.model.OrderRequestBody;
import com.seanchun.RestaurantApi.model.Table;
import com.seanchun.RestaurantApi.services.OrderServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderController.class)
public class OrderControllerUnitTests {
    /*
        Basic Unit Testing
     */
    @Autowired
    private MockMvc mvc;

    @MockBean
    private OrderServiceImpl service;

    // Unit Tests for /orders/getAllOrders
    @Test
    public void givenGetAllOrdersReturnsNull()
            throws Exception {
        given(service.getAllOrders()).willReturn(null);

        mvc.perform(get("/orders/getAllOrders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenGetAllOrdersReturnsEmptyList()
            throws Exception {
        given(service.getAllOrders()).willReturn(new ArrayList<>());

        mvc.perform(get("/orders/getAllOrders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenGetAllOrdersThrowsError()
            throws Exception {
        given(service.getAllOrders()).willThrow(new MongoException("Mongo Error"));

        mvc.perform(get("/orders/getAllOrders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    // Unit Tests for /orders/getOrdersForTable
    @Test
    public void givenGetOrdersForTableBadParameter()
            throws Exception {
        mvc.perform(get("/orders/getOrdersForTable?tableList=")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenGetOrdersForTableReturnsNullOrEmpty()
            throws Exception {
        given(service.getOrdersForTable(Arrays.asList(0,2))).willReturn(null);
        mvc.perform(get("/orders/getOrdersForTable?tableList=0,2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        given(service.getOrdersForTable(Arrays.asList(0,2))).willReturn(new ArrayList<>());
        mvc.perform(get("/orders/getOrdersForTable?tableList=0,2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenGetOrdersForTableReturnsError()
            throws Exception {
        given(service.getOrdersForTable(Arrays.asList(0,2))).willThrow(new MongoException("Mongo Error"));
        mvc.perform(get("/orders/getOrdersForTable?tableList=0,2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    // Unit Tests for /orders/getItemForTable
    @Test
    public void givenGetItemForTableReturnsNullOrEmpty()
            throws Exception {
        given(service.getOrderForItemAndTable(0, "Ramen")).willReturn(null);
        mvc.perform(get("/orders/getItemForTable?tableNumber=0&item=Ramen")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        given(service.getOrderForItemAndTable(0, "Ramen")).willReturn(
                new Table(0, new ArrayList<>()
                ));
        mvc.perform(get("/orders/getItemForTable?tableNumber=0&item=Ramen")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenGetItemForTableReturnsError()
            throws Exception {
        given(service.getOrderForItemAndTable(0, "Ramen")).willThrow(new MongoException("Mongo Error"));
        mvc.perform(get("/orders/getItemForTable?tableNumber=0&item=Ramen")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    // Unit Tests for /orders/createOrder
    @Test
    public void givenCreateOrderBadRequest()
            throws Exception {
        OrderRequestBody orderRequestBody1 = new OrderRequestBody(0, new ArrayList<>());
        OrderRequestBody orderRequestBody2 = new OrderRequestBody(0, Arrays.asList(null, null));

        mvc.perform(MockMvcRequestBuilders.post("/orders/createOrder")
                .content(asJsonString(orderRequestBody1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());

        mvc.perform(MockMvcRequestBuilders.post("/orders/createOrder")
                .content(asJsonString(orderRequestBody2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void givenCreateOrderThrowsError() throws Exception {
        OrderRequestBody orderRequestBody = new OrderRequestBody(0, Arrays.asList("Curry"));

        given(service.addNewOrder(orderRequestBody)).willThrow(new MongoException("Mongo Error"));

        mvc.perform(MockMvcRequestBuilders.post("/orders/createOrder")
                .content(asJsonString(orderRequestBody))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isInternalServerError());
    }

    // Unit Tests for /orders/deleteOrder
    @Test
    public void givenDeleteOrderBadRequest()
            throws Exception {
        DeleteOrderRequestBody deleteRequestBody = new DeleteOrderRequestBody(0, null);

        mvc.perform(MockMvcRequestBuilders.post("/orders/deleteOrder")
                .content(asJsonString(deleteRequestBody))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void givenDeleteOrderThrowsError()
            throws Exception {
        DeleteOrderRequestBody deleteRequestBody = new DeleteOrderRequestBody(0, "elkfnaiow121");
        given(service.removeItemForTable(deleteRequestBody.getTableNumber(), deleteRequestBody.getUID()))
                .willThrow(new MongoException("Mongo Error"));

        mvc.perform(MockMvcRequestBuilders.post("/orders/deleteOrder")
                .content(asJsonString(deleteRequestBody))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isInternalServerError());
    }



    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
