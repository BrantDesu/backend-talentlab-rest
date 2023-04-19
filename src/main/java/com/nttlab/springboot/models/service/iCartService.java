package com.nttlab.springboot.models.service;

import org.springframework.stereotype.Repository;

import com.nttlab.springboot.models.entity.Cart;
import com.nttlab.springboot.models.entity.CartItem;

import java.util.List;


@Repository
public interface iCartService {

	public List<Cart> findAll();
	
	public Cart findOne(Long id);
	    
    public Cart save(Cart cart);
	    
    public void delete(Cart cart);
	    
    public void deleteAll(Cart cart);
	
}
