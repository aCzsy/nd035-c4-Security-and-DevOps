package com.example.demo.controllers;

import java.util.List;

import com.example.demo.model.requests.AddItemRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

@RestController
@RequestMapping("/api/item")
public class ItemController {

	@Autowired
	private ItemRepository itemRepository;

	final Logger logger = LogManager.getLogger(ItemController.class);

	@PostMapping("/addItem")
	public ResponseEntity<Item> addItem(@RequestBody AddItemRequest addItemRequest){
		Item item = new Item();
		item.setName(addItemRequest.getName());
		item.setPrice(addItemRequest.getPrice());
		item.setDescription(addItemRequest.getDescription());

		itemRepository.save(item);
		logger.info("ITEM_ADD_REQ_SUCCESS: {} successfully added", item.getName());

		return ResponseEntity.ok(item);
	}

	@GetMapping
	public ResponseEntity<List<Item>> getItems() {
		return ResponseEntity.ok(itemRepository.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {
		return ResponseEntity.of(itemRepository.findById(id));
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
		List<Item> items = itemRepository.findByName(name);
		return items == null || items.isEmpty() ? ResponseEntity.notFound().build()
				: ResponseEntity.ok(items);
			
	}
	
}
