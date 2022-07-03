package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.requests.AddItemRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController,"itemRepository",itemRepository);
    }

    @Test
    public void findItems(){
        ResponseEntity<List<Item>> items = itemController.getItems();
        assertNotNull(items);
        assertEquals(200, items.getStatusCodeValue());
        assertNotNull(items.getBody());
        assertEquals(0, items.getBody().size());
    }

    @Test
    public void findItemByIdHappyPath(){
        Item item = new Item(1L,"Chair", BigDecimal.valueOf(5.99),"Wooden Chair");
        //Item savedItem = itemRepository.save(item);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Item foundItem = response.getBody();
        assertNotNull(foundItem);
        assertEquals(Long.valueOf(1L), foundItem.getId());
        assertEquals("Chair", foundItem.getName());
        assertEquals("Wooden Chair", foundItem.getDescription());

        verify(itemRepository).findById(1L);
    }

    @Test
    public void findItemByIdDoesntExist(){
        ResponseEntity<Item> res = itemController.getItemById(1L);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    public void findItemsByNameHappyPath(){
        List<Item> items = new ArrayList<>();

        Item item1 = new Item(1L,"Chair",BigDecimal.valueOf(5.99), "Wooden Chair");
        Item item2 = new Item(2L, "Chair", BigDecimal.valueOf(6.99), "Thick Wooden Chair");

        items.add(item1);
        items.add(item2);

        when(itemRepository.findByName("Chair")).thenReturn(items);

        ResponseEntity<List<Item>> resItems = itemController.getItemsByName("Chair");
        assertNotNull(resItems);
        assertEquals(200, resItems.getStatusCodeValue());

        List<Item> foundItems = resItems.getBody();
        assertNotNull(foundItems);
        assertEquals(Long.valueOf(1L), foundItems.get(0).getId());
        assertEquals(BigDecimal.valueOf(5.99), foundItems.get(0).getPrice());
        assertEquals("Wooden Chair", foundItems.get(0).getDescription());
        assertEquals(Long.valueOf(2L), foundItems.get(1).getId());
        assertEquals(BigDecimal.valueOf(6.99), foundItems.get(1).getPrice());
        assertEquals("Thick Wooden Chair", foundItems.get(1).getDescription());

        verify(itemRepository).findByName("Chair");
    }

}
