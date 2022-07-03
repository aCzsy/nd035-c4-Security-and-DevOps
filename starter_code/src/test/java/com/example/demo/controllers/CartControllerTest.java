package com.example.demo.controllers;

import com.example.demo.TestUtils;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class CartControllerTest {
    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    User user;
    Item item;
    ModifyCartRequest req;

    @Before
    public void setup(){
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);

        user = createUser();
        item = createItem();
        req = createReq();
    }

    @Test
    public void testAddToCartHappyPath() throws IOException {
        when(userRepository.findByUsername(req.getUsername())).thenReturn(user);
        when(itemRepository.findById(req.getItemId())).thenReturn(Optional.of(item));

        ResponseEntity<Cart> cartResponseEntity = cartController.addTocart(req);

        assertNotNull(cartResponseEntity);
        assertEquals(200, cartResponseEntity.getStatusCodeValue());

        Cart cart = cartResponseEntity.getBody();
        assertNotNull(cart);
        assertEquals(3, cart.getItems().size());

        verify(userRepository).findByUsername(req.getUsername());
        verify(itemRepository).findById(req.getItemId());
    }

    @Test
    public void testRemoveFromCartHappyPath() throws IOException {
        when(userRepository.findByUsername(req.getUsername())).thenReturn(user);
        when(itemRepository.findById(req.getItemId())).thenReturn(Optional.of(item));

        ResponseEntity<Cart> cartAddReq = cartController.addTocart(req);

        assertNotNull(cartAddReq);
        assertEquals(200, cartAddReq.getStatusCodeValue());

        Cart cart = cartAddReq.getBody();
        assertNotNull(cart);
        assertEquals(3, cart.getItems().size());


        ResponseEntity<Cart> cartRemoveReq = cartController.removeFromcart(req);
        assertNotNull(cartRemoveReq);
        assertEquals(200, cartRemoveReq.getStatusCodeValue());

        Cart cart2 = cartRemoveReq.getBody();
        assertNotNull(cart2);
        assertEquals(0, cart2.getItems().size());

        verify(userRepository, times(2)).findByUsername(req.getUsername());
        verify(itemRepository, times(2)).findById(req.getItemId());
    }

    @Test
    public void testAddToCartNoUser() throws IOException {
        when(userRepository.findByUsername(req.getUsername())).thenReturn(null);

        ResponseEntity<Cart> cartResponseEntity = cartController.addTocart(req);

        assertNotNull(cartResponseEntity);
        assertEquals(404, cartResponseEntity.getStatusCodeValue());

        verify(userRepository).findByUsername(req.getUsername());
    }

    @Test
    public void testAddToCardNoItem() throws IOException{
        when(userRepository.findByUsername(req.getUsername())).thenReturn(user);
        when(itemRepository.findById(req.getItemId())).thenReturn(Optional.empty());

        ResponseEntity<Cart> cartResponseEntity = cartController.addTocart(req);

        assertNotNull(cartResponseEntity);
        assertEquals(404, cartResponseEntity.getStatusCodeValue());

        verify(userRepository).findByUsername(req.getUsername());
        verify(itemRepository).findById(req.getItemId());
    }

    @Test
    public void testRemoveCartNoUser() throws IOException{
        when(userRepository.findByUsername(req.getUsername())).thenReturn(user);
        when(itemRepository.findById(req.getItemId())).thenReturn(Optional.of(item));

        ResponseEntity<Cart> cartAddReq = cartController.addTocart(req);

        assertNotNull(cartAddReq);
        assertEquals(200, cartAddReq.getStatusCodeValue());

        Cart cart = cartAddReq.getBody();
        assertNotNull(cart);
        assertEquals(3, cart.getItems().size());

        when(userRepository.findByUsername(req.getUsername())).thenReturn(null);

        ResponseEntity<Cart> cartRemoveReq = cartController.removeFromcart(req);

        assertNotNull(cartRemoveReq);
        assertEquals(404, cartRemoveReq.getStatusCodeValue());

        verify(userRepository, times(2)).findByUsername(req.getUsername());
    }

    @Test
    public void testRemoveCartNoItem() throws IOException{
        when(userRepository.findByUsername(req.getUsername())).thenReturn(user);
        when(itemRepository.findById(req.getItemId())).thenReturn(Optional.of(item));

        ResponseEntity<Cart> cartAddReq = cartController.addTocart(req);

        assertNotNull(cartAddReq);
        assertEquals(200, cartAddReq.getStatusCodeValue());

        Cart cart = cartAddReq.getBody();
        assertNotNull(cart);
        assertEquals(3, cart.getItems().size());

        //when(userRepository.findByUsername(req.getUsername())).thenReturn(user);
        when(itemRepository.findById(req.getItemId())).thenReturn(Optional.empty());

        ResponseEntity<Cart> cartRemoveReq = cartController.removeFromcart(req);

        assertNotNull(cartRemoveReq);
        assertEquals(404, cartRemoveReq.getStatusCodeValue());

        verify(userRepository, times(2)).findByUsername(req.getUsername());
        verify(itemRepository,times(2)).findById(req.getItemId());
    }

    public User createUser(){
        User user = new User();
        user.setId(1L);
        user.setUsername("Artis");
        user.setPassword("password");
        user.setCart(new Cart());

        return user;
    }

    public Item createItem(){
        Item item = new Item();
        item.setId(1L);
        item.setName("Photo frame");
        item.setPrice(BigDecimal.valueOf(3.99));
        item.setDescription("Frame for your photo");

        return item;
    }

    public ModifyCartRequest createReq(){
        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("Artis");
        req.setItemId(1L);
        req.setQuantity(3);

        return req;
    }

}
