package com.nttlab.springboot.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.nttlab.springboot.models.entity.Cart;
import com.nttlab.springboot.models.entity.CartItem;
import com.nttlab.springboot.models.service.iCartService;
import com.nttlab.springboot.models.service.iUserService;

@Controller
public class CartController {

	@Autowired
	private iCartService cartService;
	
	@Autowired
	private iUserService clientService;
	
	@GetMapping(value= "/cart")
	public String userCart(Model model) {
		model.addAttribute("title","Carrito de Compra");
		model.addAttribute("cart", clientService.findByEmail("admin@admin.com").getCart());
		return "cart";

	}
	
}
