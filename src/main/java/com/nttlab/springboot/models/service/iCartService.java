package com.nttlab.springboot.models.service;

import org.springframework.stereotype.Repository;

import com.nttlab.springboot.models.entity.Cart;
import com.nttlab.springboot.models.entity.CartItem;

import java.util.List;


@Repository
public interface iCartService {

	public List<CartItem> findAll();
	
	public Cart findOne(Long id);
	    
    public void save(CartItem cartItem);
	    
    public void delete(CartItem cartItem);
	    
    public void deleteAll(Cart cart);
	
}
