package com.nttlab.springboot.controllers.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nttlab.springboot.models.entity.Cart;
import com.nttlab.springboot.models.entity.CartItem;
import com.nttlab.springboot.models.entity.Client;
import com.nttlab.springboot.models.entity.Product;
import com.nttlab.springboot.models.entity.Sale;
import com.nttlab.springboot.models.service.iCartService;
import com.nttlab.springboot.models.service.iProductService;
import com.nttlab.springboot.models.service.iSaleService;
import com.nttlab.springboot.models.service.iUserService;

//@CrossOrigin(origins = {"http://localhost:8088", "http://localhost:4200"})
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/api")
public class SaleRestController {
	@Autowired
	private iSaleService saleService;
	
	@Autowired
	private iProductService productService;

	@Autowired
	private iCartService cartService;
	
	@Autowired
	private iUserService clientService;
	
	@GetMapping(value = {"/sales"}, produces = "application/json")
	public ResponseEntity<?> getAllSales(){
	    List<Sale> sales = null;
	    Map<String, Object> response = new HashMap<>();
	    try {
	        sales = saleService.findAll();
	        if(sales.isEmpty()) {
	            response.put("mensaje", "No hay ventas registradas en la base de datos");
	        } else {
	            response.put("mensaje", "Actualmente la base de datos cuenta con " + sales.size() + " registros");
	            response.put("sales", sales);
	        }
	        return new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK);
	    }
	    catch(DataAccessException ex) {
	        response.put("mensaje", "Error al realizar la consulta");
	        response.put("error", ex.getMessage() + ": " + ex.getMostSpecificCause().getMessage());
	        return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }       
	}
	
	@GetMapping(value = {"/sales/{id}"}, produces = "application/json")
	public ResponseEntity<?> getSaleById(@PathVariable(value = "id") Long id){
	    Sale sale = null;
	    Map<String, Object> response = new HashMap<>();
	    try {
	        sale = saleService.findOne(id);
	        if(sale == null) {
	            response.put("mensaje", "El id ("+ id +") de la venta solicitada no existe");
	        } else {
	            response.put("mensaje", "Venta encontrada");
	            response.put("sale", sale);
	        }
	        return new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK);
	    }
	    catch(DataAccessException ex) {
	        response.put("mensaje", "Error al realizar la consulta");
	        response.put("error", ex.getMessage() + ": " + ex.getMostSpecificCause().getMessage());
	        return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }       
	}
	
	@PostMapping(value = "/sales")
	public ResponseEntity<?> createSale(@RequestParam(name="cart_id") Long cart_id){
		Map<String, Object> response = new HashMap<>();
		try {
			Cart cart = cartService.findOne(cart_id);
	        
	        if(cart == null) {
	            response.put("mensaje", "Error, carrito no encontrado");
	        } else {
	        	
	        	for (CartItem ci : cart.getCart_items()) {
					if(ci.getQuantity() > ci.getProduct().getStock()) {
						response.put("mensaje", "Error, el producto " + ci.getProduct().getName() + ", no tiene suficiente stock.");
						return new ResponseEntity<Map<String,Object>>(response, HttpStatus.BAD_REQUEST);
					} else {
						Product p = productService.findOne(ci.getProduct().getIdProduct());
						p.setStock(p.getStock() - ci.getQuantity());
						productService.save(p);
					}
				}
	        	Sale sale = new Sale(cart);
	        	Sale new_sale = saleService.save(sale);
	        	
	            response.put("mensaje", "La venta se ha procesado correctamente.");
	            response.put("sale", new_sale);
	            
	            cart.setActive(false);
	            cartService.save(cart);
	            
	            Cart new_cart = new Cart(cart.getClient());
	            cartService.save(new_cart);
	        }

	        return new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK);
	    }
	    catch(DataAccessException ex) {
	        response.put("mensaje", "Error al realizar la consulta");
	        response.put("error", ex.getMessage() + ": " + ex.getMostSpecificCause().getMessage());
	        return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
}
