package com.nttlab.springboot.controllers.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nttlab.springboot.models.entity.Client;
import com.nttlab.springboot.models.entity.Sale;
import com.nttlab.springboot.models.service.iCartService;
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
	private iCartService cartService;
	
	@Autowired
	private iUserService clientService;
	
	@GetMapping(value = {"/sales"}, produces = "application/json")
	public ResponseEntity<?> getAllUsers(){
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
}
