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
import org.springframework.web.bind.annotation.RestController;

import com.nttlab.springboot.models.entity.Product;
import com.nttlab.springboot.models.service.iProductService;
import com.nttlab.springboot.util.validator.EmailValidator;
import com.nttlab.springboot.util.validator.RutValidator;

//@CrossOrigin(origins = {"http://localhost:8088", "http://localhost:4200"})
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/api")
public class ProductRestController {
	
	@Autowired
	private iProductService productService;
	
	@GetMapping(value = {"/products"}, produces = "application/json")
	public ResponseEntity<?> getAllProducts(){
		List<Product> products = null;
		Map<String, Object> response = new HashMap<>();
		try {
			products = productService.findAll();
			if(products.isEmpty()) {
				response.put("mensaje", "No hay products registrados en la base de datos");
				response.put("products", products);
				return new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK);
			}
			response.put("mensaje", "Actualmente la base de datos cuenta con " + products.size() + " registros");
			response.put("products", products);
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK);
		}
		catch(DataAccessException ex) {
			response.put("mensaje", "Error al realizar la consulta");
			response.put("error", ex.getMessage() + ": " + ex.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}		
	}
	
	@GetMapping(value = "/products/{id}", produces = "application/json")
	public ResponseEntity<?> getProductById(@PathVariable(value = "id", required = false) Long id){
		Product product = null;
		Map<String, Object> response = new HashMap<>();
		try {
			product = productService.findOne(id);
			if(product == null) {
				response.put("mensaje","El product ID: " + id + " no existen en la base de datos.");
				return new ResponseEntity<Map<String,Object>>(response, HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<Product>(product, HttpStatus.OK);
		}
		catch(DataAccessException ex) {
			response.put("mensaje", "Error al realizar la consulta");
			response.put("error", ex.getMessage() + ": " + ex.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value= "/products", produces = "application/json")
	public ResponseEntity<?> createProduct(@RequestBody Product product){
		Product new_product = null;
		Map<String, Object> response = new HashMap<>();
		try {
		
			List<Product> products = productService.findAll();
			for(Product a: products) {
				if(a.getName().equalsIgnoreCase(product.getName())) {
					response.put("mensaje", "Error al realizar el registro del producto. El nombre indicado ya existe en nuestros registros.");
					return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
				}
			}
			System.out.println();
			new_product = productService.save(product);
			response.put("mensaje", "Producto registrado satisfactoriamente.");
			response.put("product", new_product);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
		}
		catch(DataAccessException ex) {
			response.put("mensaje", "Error al realizar el proceso de registro de un nuevo producto.");
			response.put("error", ex.getMessage() + ": " + ex.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping(value = "/products/{id}", produces = "application/json")
	public ResponseEntity<?> updateProduct(@RequestBody Product product, @PathVariable Long id){
		Product updated_product = null;
		Product actual_product = null;
		Map<String, Object> response = new HashMap<>();
		try {
			actual_product = productService.findOne(id);
			if(actual_product == null) {
				response.put("mensaje", "No se pudo completar el proceso de actualización ya que el producto (ID " + id + ") no existe en nuestros registros");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
			
			List<Product> products = productService.findAll();
			for(Product a: products) {
				if(actual_product.getIdProduct() != a.getIdProduct()) {
					if(a.getName().equalsIgnoreCase(product.getName())) {
						response.put("mensaje", "Error al realizar el registro del producto. El nombre indicado ya existe en nuestros registros.");
						return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
					}
				}
			}
			
			actual_product.setName(product.getName());
			actual_product.setCategory(product.getCategory());
			actual_product.setPrice(product.getPrice());
			actual_product.setStock(product.getStock());

			updated_product = productService.save(actual_product);
			response.put("mensaje", "Producto modificado satisfactoriamente.");
			response.put("product", updated_product);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
			
		}
		catch(DataAccessException ex) {
			response.put("mensaje", "Error al realizar el proceso de edición del producto.");
			response.put("error", ex.getMessage() + ": " + ex.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping(value = "/products/{id}", produces = "application/json")
	public ResponseEntity<?> deleteProductById(@PathVariable Long id){
		System.out.println("delete product");
		Map<String,Object> response = new HashMap<>();
		try {
			Product a = productService.findOne(id);
			if(a != null) {
				productService.delete(id);
				response.put("mensaje", "Se ha eliminado correctamente el product de nuestros registros");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
			}
			else {
				response.put("mensaje", "Error al realizar la eliminación de los datos del product. El product no se encuentra en nuestros registros.");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
		}
		catch(DataAccessException ex) {
			response.put("mensaje", "Error al realizar el proceso de eliminación de los product.");
			response.put("error", ex.getMessage() + ": " + ex.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	

}
