package com.example.demo.controllers;

import com.example.demo.TestUtils;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class OrderControllerTest {
    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    User user;
    UserOrder userOrder;

    @Before
    public void setup(){
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);

        user = createUserWithCart();
        userOrder = createOrder();
    }

    @Test
    public void orderSubmitHappyPath(){
        when(userRepository.findByUsername("Artis")).thenReturn(user);

        ResponseEntity<UserOrder> req = orderController.submit("Artis");
        assertNotNull(req);
        assertEquals(200, req.getStatusCodeValue());

        UserOrder userOrder = req.getBody();
        assertNotNull(userOrder);
        assertEquals(userOrder.getItems(), userOrder.getItems());
        assertEquals(userOrder.getUser(),userOrder.getUser());
        assertEquals(userOrder.getTotal(), userOrder.getTotal());

        verify(userRepository).findByUsername("Artis");
    }

    @Test
    public void orderSubmitNoUser(){
        when(userRepository.findByUsername("Artis")).thenReturn(null);

        ResponseEntity<UserOrder> req = orderController.submit("Artis");
        assertNotNull(req);
        assertEquals(404, req.getStatusCodeValue());
    }

    @Test
    public void testGetOrderHistoryForUserHappyPath(){
        when(userRepository.findByUsername("Artis")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(Arrays.asList(userOrder));

        ResponseEntity<List<UserOrder>> req = orderController.getOrdersForUser("Artis");
        assertNotNull(req);
        assertEquals(200, req.getStatusCodeValue());

        List<UserOrder> orders = req.getBody();
        assertNotNull(orders);
        assertEquals(2, orders.get(0).getItems().size());
        assertEquals(user, orders.get(0).getUser());
        assertEquals(userOrder.getTotal(), orders.get(0).getTotal());

        verify(userRepository).findByUsername("Artis");
        verify(orderRepository).findByUser(user);
    }

    @Test
    public void testGetOrderHistoryForUserNoUser(){
        when(userRepository.findByUsername("Artis")).thenReturn(null);
        when(orderRepository.findByUser(user)).thenReturn(Arrays.asList(userOrder));

        ResponseEntity<List<UserOrder>> req = orderController.getOrdersForUser("Artis");
        assertNotNull(req);
        assertEquals(404, req.getStatusCodeValue());

        verify(userRepository).findByUsername("Artis");
    }

    public User createUserWithCart(){
        User user = new User();
        user.setId(1L);
        user.setUsername("Artis");
        user.setPassword("password");

        Cart cart = new Cart();
        Item chair = new Item(1L, "Chair", BigDecimal.valueOf(5.99), "Wooden chair");
        Item table = new Item(2L, "Table", BigDecimal.valueOf(10.99), "Wooden table");
        cart.addItem(chair);
        cart.addItem(table);

        user.setCart(cart);
        cart.setUser(user);

        return user;
    }

    public UserOrder createOrder(){
        UserOrder userOrder = new UserOrder();
        userOrder.setId(1L);
        userOrder = UserOrder.createFromCart(user.getCart());

        return userOrder;
    }
}
