package com.nttlab.springboot.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.nttlab.springboot.models.service.iUserService;

public class CartRestController {

	@Autowired
	private iUserService clientService;
	
	@GetMapping(value= "/cart")
	public String userCart(Model model) {
		model.addAttribute("title","Carrito de Compra");
		//model.addAttribute("cart", clientService.findByEmail("admin@admin.com").getCart());
		return "cart";

	}
}
