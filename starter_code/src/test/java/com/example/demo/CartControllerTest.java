package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);

    @Before
    public void setup(){
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
    }

    @Test
    // this is to test addToCart method in standard case
    public void addTest(){
        // stubbing
        // items and cart
        Item book2 = new Item("Book2", BigDecimal.valueOf(21.99), "This is book2");
        book2.setId(2L);
        Item laptop1 = new Item("Laptop1", BigDecimal.valueOf(1000.16), "This is the latest laptop1");
        laptop1.setId(5L);
        Cart testCart = new Cart();
        testCart.setId(8L);
        testCart.setItems(new ArrayList<>(Arrays.asList(book2, laptop1)));
        testCart.setTotal(BigDecimal.valueOf(1022.15));

        // user
        User testUser = new User();
        testUser.setId(0L);
        testUser.setUsername("test");
        testUser.setPassword("thisIsHashed");
        testCart.setUser(testUser);
        testUser.setCart(testCart);

        when(userRepository.findByUsername("test")).thenReturn(testUser);

        // create modifyCartRequest
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(9L);
        modifyCartRequest.setUsername("test");
        modifyCartRequest.setQuantity(2);
        Item book1 = new Item("Book1", BigDecimal.valueOf(2.99), "This is book1");
        book1.setId(9L);
        when(itemRepository.findById(9L)).thenReturn(Optional.of(book1));

        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        // check if response is not null
        assertNotNull(response);
        // check if response is successful
        assertEquals(200, response.getStatusCodeValue());

        // check if the returned cart is correct
        // update cart item
        testCart.setItems(Arrays.asList(book2, laptop1, book1, book1));
        testCart.setTotal(BigDecimal.valueOf(1028.13));

        Cart returnedCart = response.getBody();
        assertEquals(testCart.getId(), returnedCart.getId());
        assertEquals(testCart.getUser(), returnedCart.getUser());
        assertEquals(testCart.getTotal(), returnedCart.getTotal());
        assertArrayEquals(testCart.getItems().toArray(), returnedCart.getItems().toArray());
    }

    @Test
    // this is to test addToCart method when user is null
    public void addTestUserNull(){
        // stubbing
        User testUser = null;
        when(userRepository.findByUsername("test")).thenReturn(testUser);

        // create modifyCartRequest
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("test");

        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        // check if response is not null
        assertNotNull(response);
        // check if response is not found (404)
        assertEquals(404, response.getStatusCodeValue());
    }

    
    @Test
    // this is to test removeFromcart method in standard status
    public void removeTest(){
        // stubbing
        // items and cart
        Item book1 = new Item("book1", BigDecimal.valueOf(5.45), "this is book 1");
        book1.setId(2L);
        Item laptop4 = new Item("laptop4", BigDecimal.valueOf(599.99), "this is laptop 4");
        laptop4.setId(9L);
        Cart testCart = new Cart();
        testCart.setId(0L);
        testCart.setItems(new ArrayList<>(Arrays.asList(book1, laptop4)));
        testCart.setTotal(BigDecimal.valueOf(605.44));

        // user
        User testUser = new User();
        testUser.setId(4L);
        testUser.setUsername("test");
        testUser.setPassword("thisIsHashed");
        testUser.setCart(testCart);
        testCart.setUser(testUser);

        when(userRepository.findByUsername("test")).thenReturn(testUser);
        when(itemRepository.findById(9L)).thenReturn(Optional.of(laptop4));

        // create modifyCartRequest
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(9L);
        modifyCartRequest.setUsername("test");
        modifyCartRequest.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        // check if response is not null
        assertNotNull(response);
        // check if response is successful
        assertEquals(200, response.getStatusCodeValue());

        // update cart items and total
        testCart.setItems(Arrays.asList(book1));
        testCart.setTotal(BigDecimal.valueOf(5.45));

        // check if cart is correct
        Cart returnedCart = response.getBody();
        assertEquals(testCart.getId(), returnedCart.getId());
        assertEquals(testCart.getUser(), returnedCart.getUser());
        assertEquals(testCart.getTotal(), returnedCart.getTotal());
        assertArrayEquals(testCart.getItems().toArray(), returnedCart.getItems().toArray());
    }

    @Test
    // this is to test removeFromCart method when user is null
    public void removeTestNullUser(){
        User testUser = null;
        when(userRepository.findByUsername("test")).thenReturn(testUser);

        // create modifyCartRequest
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("test");

        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        // check if response is not null
        assertNotNull(response);
        // check if response is not found (404)
        assertEquals(404, response.getStatusCodeValue());
    }
}
