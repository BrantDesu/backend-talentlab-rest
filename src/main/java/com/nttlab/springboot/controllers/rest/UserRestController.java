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

import com.nttlab.springboot.models.entity.Client;
import com.nttlab.springboot.models.service.iUserService;
import com.nttlab.springboot.util.validator.EmailValidator;
import com.nttlab.springboot.util.validator.RutValidator;

//@CrossOrigin(origins = {"http://localhost:8088", "http://localhost:4200"})
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(value = "/api")
public class UserRestController {
	
	@Autowired
	private iUserService userService;
	
	@GetMapping(value = {"/users"}, produces = "application/json")
	public ResponseEntity<?> getAllUsers(){
	    List<Client> clients = null;
	    Map<String, Object> response = new HashMap<>();
	    try {
	        clients = userService.findAll();
	        if(clients.isEmpty()) {
	            response.put("mensaje", "No hay usuarios registrados en la base de datos");
	        } else {
	            response.put("mensaje", "Actualmente la base de datos cuenta con " + clients.size() + " registros");
	            response.put("users", clients);
	        }
	        return new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK);
	    }
	    catch(DataAccessException ex) {
	        response.put("mensaje", "Error al realizar la consulta");
	        response.put("error", ex.getMessage() + ": " + ex.getMostSpecificCause().getMessage());
	        return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }       
	}

	@GetMapping(value = "/users/{id}", produces = "application/json")
	public ResponseEntity<?> getUserById(@PathVariable(value = "id", required = false) Long id){
		Client client = null;
		Map<String, Object> response = new HashMap<>();
		try {
			client = userService.findOne(id);
			if(client == null) {
				response.put("mensaje","El client ID: " + id + " no existen en la base de datos.");
				return new ResponseEntity<Map<String,Object>>(response, HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<Client>(client, HttpStatus.OK);
		}
		catch(DataAccessException ex) {
			response.put("mensaje", "Error al realizar la consulta");
			response.put("error", ex.getMessage() + ": " + ex.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(value = "/users/email", produces = "application/json")
	public ResponseEntity<?> getUserByEmail(@RequestParam(value = "email") String email){
		Client client = null;
		Map<String, Object> response = new HashMap<>();
		try {
			client = userService.findByEmail(email);
			if(client == null) {
				response.put("mensaje","El cliente con correo: " + email + " no existen en la base de datos.");
				return new ResponseEntity<Map<String,Object>>(response, HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<Client>(client, HttpStatus.OK);
		}
		catch(DataAccessException ex) {
			response.put("mensaje", "Error al realizar la consulta");
			response.put("error", ex.getMessage() + ": " + ex.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value= "/users", produces = "application/json")
	public ResponseEntity<?> createUser(@RequestBody Client client){
		Client client_nuevo = null;
		Map<String, Object> response = new HashMap<>();
		try {
			boolean rutValido = new RutValidator().isValid(client.getRut(),null);
			if(!rutValido) {
				response.put("mensaje", "Error al realizar el registro del usuario. El rut ingresado no es válido.");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
			}
			
			boolean emailValido = new EmailValidator().isValid(client.getEmail(), null);
			if(!emailValido) {
				response.put("mensaje", "Error al realizar el registro del usuario. El email ingresado no es válido.");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
			}

			List<Client> clients = userService.findAll();
			for(Client a: clients) {
				if(a.getEmail().equalsIgnoreCase(client.getEmail())) {
					response.put("mensaje", "Error al realizar el registro del usuario. El correo indicado ya existe en nuestros registros.");
					return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
				}
				if(a.getRut().equalsIgnoreCase(client.getRut())) {
					response.put("mensaje", "Error al realizar el registro del usuario. El rut indicado ya existe en nuestros registros.");
					return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
				}
			}
			
			client_nuevo = userService.save(client);
			response.put("mensaje", "Usuario registrado satisfactoriamente.");
			response.put("user", client_nuevo);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
		}
		catch(DataAccessException ex) {
			response.put("mensaje", "Error al realizar el proceso de registro de un nuevo usuario.");
			response.put("error", ex.getMessage() + ": " + ex.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping(value = "/users/{id}", produces = "application/json")
	public ResponseEntity<?> updateUser(@RequestBody Client client, @PathVariable Long id){
		Client updated_client = null;
		Client actual_client = null;
		Map<String, Object> response = new HashMap<>();
		try {
			actual_client = userService.findOne(id);
			if(actual_client == null) {
				response.put("mensaje", "No se pudo completar el proceso de actualización ya que el usuario (ID " + id + ") no existe en nuestros registros");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
			
			boolean rutValido = new RutValidator().isValid(client.getRut(),null);
			if(!rutValido) {
				response.put("mensaje", "Error al realizar la edición del usuario. El rut ingresado no es válido.");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
			}
			
			
			boolean emailValido = new EmailValidator().isValid(client.getEmail(), null);
			if(!emailValido) {
				response.put("mensaje", "Error al realizar el registro del usuario. El email ingresado no es válido.");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
			}
			
			List<Client> clients = userService.findAll();
			for(Client a: clients) {
				if(actual_client.getIdUser() != a.getIdUser()) {
					if(a.getEmail().equalsIgnoreCase(client.getEmail())) {
						response.put("mensaje", "Error al realizar el registro del usuario. El correo indicado ya existe en nuestros registros.");
						return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
					}
					if(a.getRut().equalsIgnoreCase(client.getRut())) {
						response.put("mensaje", "Error al realizar el registro del usuario. El rut indicado ya existe en nuestros registros.");
						return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
					}
				}
			}
			
			actual_client.setRut(client.getRut());
			actual_client.setName(client.getName());
			actual_client.setLastName(client.getLastName());
			actual_client.setEmail(client.getEmail());
			actual_client.setPassword(client.getPassword());
			actual_client.setAuthority(client.getAuthority());
			updated_client = userService.save(actual_client);
			response.put("mensaje", "Usuario modificado satisfactoriamente.");
			response.put("user", updated_client);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
			
		}
		catch(DataAccessException ex) {
			response.put("mensaje", "Error al realizar el proceso de edición del usuario.");
			response.put("error", ex.getMessage() + ": " + ex.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping(value = "/users/{id}", produces = "application/json")
	public ResponseEntity<?> deleteUserById(@PathVariable Long id){
		Map<String,Object> response = new HashMap<>();
		try {
			Client a = userService.findOne(id);
			if(a != null) {
				userService.delete(id);
				response.put("mensaje", "Se ha eliminado correctamente el usuario de nuestros registros");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
			}
			else {
				response.put("mensaje", "Error al realizar la eliminación de los datos del usuario. El usuario no se encuentra en nuestros registros.");
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
		}
		catch(DataAccessException ex) {
			response.put("mensaje", "Error al realizar el proceso de eliminación de los usuarios.");
			response.put("error", ex.getMessage() + ": " + ex.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	

}
