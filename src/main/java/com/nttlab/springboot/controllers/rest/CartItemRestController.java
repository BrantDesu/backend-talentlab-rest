package com.nttlab.springboot.controllers.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nttlab.springboot.models.dto.CartItemDTO;
import com.nttlab.springboot.models.entity.Cart;
import com.nttlab.springboot.models.entity.CartItem;
import com.nttlab.springboot.models.entity.Client;
import com.nttlab.springboot.models.entity.Product;
import com.nttlab.springboot.models.service.iCartItemService;
import com.nttlab.springboot.models.service.iCartService;
import com.nttlab.springboot.models.service.iProductService;
import com.nttlab.springboot.models.service.iUserService;
import com.nttlab.springboot.util.validator.EmailValidator;
import com.nttlab.springboot.util.validator.RutValidator;


//@CrossOrigin(origins = {"http://localhost:8088", "http://localhost:4200"})
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/api")
public class CartItemRestController {
	
	@Autowired
	private iCartItemService cartItemService;
	
	@Autowired
	private iCartService cartService;

	@Autowired
	private iProductService productService;
	
	@PostMapping(value= "/cart-items", produces = "application/json")
	public ResponseEntity<?> createCartItem(
			@RequestBody CartItemDTO body
		){
		
		Map<String, Object> response = new HashMap<>();
		try {
			Cart cart = cartService.findOne((long) body.getCart_id());
			if(cart == null) {
	            response.put("mensaje", "El carrito proporcionado no se encuentra en la base de datos.");
	            response.put("cart_item", null);
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
	        }
			
			Product product = productService.findOne((long) body.getProduct_id());
			if(product == null) {
	            response.put("mensaje", "El producto proporcionado no se encuentra en la base de datos.");
	            response.put("cart_item", null);
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
	        }
			
			if(body.getQuantity() > product.getStock()) {
	            response.put("mensaje", "La cantidad proporcionada {"+ body.getQuantity() +"} excede el stock {" + product.getStock() + "} del producto");
	            response.put("cart_item", null);
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
	        }
			
			CartItem cartItem = new CartItem(cart, product, body.getQuantity());
			CartItem newCartItem = cartItemService.save(cartItem);
			
			response.put("mensaje", "Producto añadido al carrito.");
			response.put("cart_item", newCartItem);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
		}
		catch(DataAccessException ex) {
			response.put("mensaje", "Error al añadir el producto al carrito.");
			response.put("error", ex.getMessage() + ": " + ex.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping(value = { "/cart-items/{id}/edit" })
	public ResponseEntity<?> editCartItem(@PathVariable(name="id") Long cartItemId, @RequestParam(name="quantity") Integer quantity ) {
		Map<String, Object> response = new HashMap<>();
		try {
			CartItem cartItem = cartItemService.findOne(cartItemId);
			if(cartItem == null) {
				response.put("mensaje", "El articulo del carrito proporcionado no se encuentra en la base de datos.");
				response.put("cart_item", null);
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
			cartItem.setQuantity(quantity);
			cartItem.calculateCartItemTotal();

			cartItemService.save(cartItem);
			response.put("mensaje", "Articulo del carrito actualizado.");
			//response.put("cart_item", newCartItem);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
		} catch(DataAccessException ex) {
			response.put("mensaje", "Error al añadir el producto al carrito.");
			response.put("error", ex.getMessage() + ": " + ex.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping(value = { "/cart-items/{id}/delete" })
	public ResponseEntity<?> deleteCartItem(@PathVariable(name="id") Long cartItemId) {
		Map<String, Object> response = new HashMap<>();
		try {
			CartItem cartItem = cartItemService.findOne(cartItemId);
			if(cartItem == null) {
				response.put("mensaje", "El articulo del carrito proporcionado no se encuentra en la base de datos.");
				response.put("cart_item", null);
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}

			cartItemService.delete(cartItem);
			response.put("mensaje", "Articulo del carrito eliminado.");
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
			
		} catch(DataAccessException ex) {
			response.put("mensaje", "Error al añadir el producto al carrito.");
			response.put("error", ex.getMessage() + ": " + ex.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
