package com.nttlab.springboot.controllers.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nttlab.springboot.models.entity.Cart;
import com.nttlab.springboot.models.service.iCartService;
import com.nttlab.springboot.models.service.iUserService;

//@CrossOrigin(origins = {"http://localhost:8088", "http://localhost:4200"})
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/api")
public class CartRestController {

	@Autowired
	private iCartService cartService;
	
	@GetMapping(value= "/carts/{cart_id}")
	public ResponseEntity<?> getCart(@PathVariable(name="cart_id") Long cart_id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Cart cart = cartService.findOne(cart_id);
			if(cart == null) {
	            response.put("mensaje", "Error, carrito no encontrado.");
	        } else {
	        	cart.calculateCartTotal();
	        	response.put("mensaje", "Carrito encontrado.");
	        	response.put("cart", cart);
	        }
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK);
		} catch(DataAccessException ex) {
			response.put("mensaje", "Error al realizar la consulta");
	        response.put("error", ex.getMessage() + ": " + ex.getMostSpecificCause().getMessage());
	        return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
