package com.example.demo;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    // this is to test getItems method
    public void getItemsTest(){
        // stubbing
        Item book1 = new Item("book1", BigDecimal.valueOf(7.99), "This is book1");
        book1.setId(0L);
        Item laptop2 = new Item("laptop2", BigDecimal.valueOf(599.99), "This is laptop2");
        laptop2.setId(2L);
        Item pen6 = new Item("pen6", BigDecimal.valueOf(0.80), "This is pen6");
        pen6.setId(1L);

        List<Item> testStock = Arrays.asList(book1, laptop2, pen6);
        when(itemRepository.findAll()).thenReturn(testStock);

        ResponseEntity<List<Item>> response = itemController.getItems();
        // check if response is not null
        assertNotNull(response);
        // check if response is successful
        assertEquals(200, response.getStatusCodeValue());

        // check if the list of items are correct
        assertArrayEquals(testStock.toArray(), response.getBody().toArray());
    }

    @Test
    // this is to test getItemById method
    public void getById(){
        // stubbing
        Item pen6 = new Item("pen6", BigDecimal.valueOf(0.80), "This is pen6");
        pen6.setId(1L);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(pen6));

        ResponseEntity<Item> response = itemController.getItemById(1L);
        // check if response is not null
        assertNotNull(response);
        // check if response is successful
        assertEquals(200, response.getStatusCodeValue());

        // check if the returned item is correct
        Item returnedItem = response.getBody();
        assertEquals(1L, returnedItem.getId().longValue());
        assertEquals("pen6", returnedItem.getName());
        assertEquals(BigDecimal.valueOf(0.80), returnedItem.getPrice());
        assertEquals("This is pen6", returnedItem.getDescription());

    }

    @Test
    // this is to test getItemsByName method in standard case
    public void getByName(){
        // stub
        Item laptop2 = new Item("laptop2", BigDecimal.valueOf(599.99), "This is laptop2");
        laptop2.setId(2L);
        Item laptop2Mini = new Item("laptop2", BigDecimal.valueOf(399.99), "This is laptop2 mini");
        laptop2Mini.setId(3L);
        List<Item> laptop2List = Arrays.asList(laptop2, laptop2Mini);
        when(itemRepository.findByName("laptop2")).thenReturn(laptop2List);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("laptop2");
        // check if response is not null
        assertNotNull(response);
        // check if response is successful
        assertEquals(200, response.getStatusCodeValue());

        // check if the returned item list is correct
        assertArrayEquals(laptop2List.toArray(), response.getBody().toArray());

    }

    @Test
    // this is to test getItemsByName method when items are null
    public void getByNameTestNullItems(){
        List<Item> testItems = null;
        when(itemRepository.findByName("test")).thenReturn(testItems);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("test");
        // check if response is not null
        assertNotNull(response);
        // check if response is not found (404)
        assertEquals(404, response.getStatusCodeValue());
    }
}
