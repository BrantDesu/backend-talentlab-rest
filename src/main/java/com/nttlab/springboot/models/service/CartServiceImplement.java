package com.nttlab.springboot.models.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nttlab.springboot.models.dao.iCartDAO;
import com.nttlab.springboot.models.dao.iCartItemDAO;
import com.nttlab.springboot.models.entity.Cart;
import com.nttlab.springboot.models.entity.CartItem;


@Service
public class CartServiceImplement implements iCartService {

    @Autowired
    private iCartDAO cartDao;

    @Override
	@Transactional(readOnly = true)
	public List<Cart> findAll() {
    	return (List<Cart>) cartDao.findAll();
	}

    @Override
	@Transactional(readOnly = true)
	public Cart findOne(Long id) {
		return cartDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Cart save(Cart cart) {
		return cartDao.save(cart);
		
	}

	@Override
	@Transactional
	public void delete(Cart cartItem) {
		cartDao.delete(cartItem);
		
	}

	@Override
	@Transactional
	public void deleteAll(Cart cart) {
		cartDao.deleteAll();
		
	}

	

    
}
