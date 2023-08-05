package com.example.demo;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setup(){
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
    }

    @Test
    // this is to test submit method in standard case
    public void submitTest(){
        // stubbing
        // create items
        Item book1 = new Item("book 1", BigDecimal.valueOf(3.79), "This is book 1");
        Item book2 = new Item("book 2", BigDecimal.valueOf(8.99), "This is book 2");

        // create cart
        Cart testCart = new Cart();
        testCart.setItems(Arrays.asList(book1, book2));
        testCart.setTotal(BigDecimal.valueOf(12.79));

        // create user
        User testUser = new User();
        testUser.setId(0L);
        testUser.setUsername("test");
        testUser.setPassword("thisIsHashed");
        testUser.setCart(testCart);
        // set cart.user
        testCart.setUser(testUser);

        // userorder
        UserOrder testOrder = UserOrder.createFromCart(testCart);

        when(userRepository.findByUsername("test")).thenReturn(testUser);
        when(orderRepository.save(testOrder)).thenReturn(testOrder);

        ResponseEntity<UserOrder> response = orderController.submit("test");
        // check if response not null
        assertNotNull(response);
        // check if response success
        assertEquals(200, response.getStatusCodeValue());

        // check if response body (userorder) is correct
        UserOrder order = response.getBody();
        assertEquals(testUser.getId(), order.getUser().getId());
        assertEquals(testUser.getUsername(), order.getUser().getUsername());
        assertEquals(testUser.getPassword(), order.getUser().getPassword());
        assertArrayEquals(new Item[]{book1, book2}, order.getItems().toArray());
        assertEquals(BigDecimal.valueOf(12.79), order.getTotal());
    }

    @Test
    // this is to test submit method when user is null
    public void submitTestNullUser(){
        User testUser = null;
        when(userRepository.findByUsername("test")).thenReturn(testUser);

        ResponseEntity<UserOrder> response = orderController.submit("test");
        // check if response not null
        assertNotNull(response);
        // check if response is not found (404)
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    // this is to test getOrdersForUser method in standard case
    public void getOrdersTest(){
        // create items
        Item book1 = new Item("book 1", BigDecimal.valueOf(3.79), "This is book 1");
        Item book2 = new Item("book 2", BigDecimal.valueOf(8.99), "This is book 2");
        Item candy = new Item("candy", BigDecimal.valueOf(5.50), "This is candy");
        Item sticker = new Item("sticker", BigDecimal.valueOf(2.99), "This is sticker");

        // user
        User testUser = new User();
        testUser.setId(10L);
        testUser.setUsername("test");
        testUser.setPassword("thisIsHashed");

        // multiple orders
        UserOrder testOrder1 = new UserOrder();
        testOrder1.setId(0L);
        testOrder1.setUser(testUser);
        testOrder1.setItems(Arrays.asList(book1, book2));
        testOrder1.setTotal(BigDecimal.valueOf(12.79));

        UserOrder testOrder2 = new UserOrder();
        testOrder2.setId(14L);
        testOrder2.setUser(testUser);
        testOrder2.setItems(Arrays.asList(candy, sticker));
        testOrder2.setTotal(BigDecimal.valueOf(8.49));

        when(userRepository.findByUsername("test")).thenReturn(testUser);
        when(orderRepository.findByUser(testUser)).thenReturn(Arrays.asList(testOrder1, testOrder2));

        // check if response is not null
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");
        assertNotNull(response);
        // check if response is success
        assertEquals(200, response.getStatusCodeValue());

        // check if body (list of orders) is correct
        List<UserOrder> orderList = response.getBody();
        assertArrayEquals(new UserOrder[]{testOrder1, testOrder2}, orderList.toArray());
    }

    @Test
    // this is to test getOrdersForUser method when user is null
    public void getOrdersTestNullUser(){
        User testUser = null;
        when(userRepository.findByUsername("test")).thenReturn(testUser);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");
        // check if response not null
        assertNotNull(response);
        // check if response is not found (404)
        assertEquals(404, response.getStatusCodeValue());
    }
}
