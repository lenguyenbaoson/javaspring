package com.mockproject.service.impl;

import java.util.HashMap;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mockproject.dto.CartDetailDto;
import com.mockproject.dto.CartDto;
import com.mockproject.entity.Orders;
import com.mockproject.entity.Products;
import com.mockproject.entity.Users;
import com.mockproject.service.CartService;
import com.mockproject.service.OrderDetailsService;
import com.mockproject.service.OrdersService;
import com.mockproject.service.ProductsService;

@Service
public class CartServiceImpl implements CartService {
	
	@Autowired
	private ProductsService productService;
	
	@Autowired
	private OrderDetailsService orderDetailService;
	
	@Autowired
	private OrdersService orderService;

	@Override
	public CartDto updateCart(CartDto cart, Long productId, Integer quantity, boolean isReplace) {
		Products product = productService.findById(productId);
		
		HashMap<Long, CartDetailDto> listDetail = cart.getListDetail();
		
		// 1- them moi sp
		// 2- update: 2.1- cong don || 2.2- thay the hoan toan (replace)
		// 3- delete: update voi SL = 0
		
		if (!listDetail.containsKey(productId)) {
			// them moi
			CartDetailDto cartDetail = addNewCartDetail(product, quantity);
			listDetail.put(productId, cartDetail);
		} else if (quantity > 0) {
			// update
			if (isReplace) {
				// thay the SL cu = SL moi
				listDetail.get(productId).setQuantity(quantity);
			} else {
				// cong don
				Integer oldQuantity = listDetail.get(productId).getQuantity();
				Integer newQuantity = oldQuantity + quantity;
				listDetail.get(productId).setQuantity(newQuantity);
			}
		} else {
			// delete
			listDetail.remove(productId);
		}
		cart.setTotalQuantity(getTotalQuantity(cart));
		cart.setTotalPrice(getTotalPrice(cart));
		return cart;
	}

	@Override
	public Integer getTotalQuantity(CartDto cart) {
		Integer totalQuantity = 0;
		HashMap<Long, CartDetailDto> listDetail = cart.getListDetail();
		for (CartDetailDto cartDetail : listDetail.values()) {
			totalQuantity += cartDetail.getQuantity();
		}
		return totalQuantity;
	}

	@Override
	public Double getTotalPrice(CartDto cart) {
		Double totalPrice = 0D;
		HashMap<Long, CartDetailDto> listDetail = cart.getListDetail();
		for (CartDetailDto cartDetail : listDetail.values()) {
			totalPrice += cartDetail.getPrice() * cartDetail.getQuantity();
		}
		return totalPrice;
	}
	
	private CartDetailDto addNewCartDetail(Products product, Integer quantity) {
		CartDetailDto cartDetail = new CartDetailDto();
		cartDetail.setProductId(product.getId());
		cartDetail.setQuantity(quantity);
		cartDetail.setPrice(product.getPrice());
		cartDetail.setName(product.getName());
		cartDetail.setImgUrl(product.getImgUrl());
		cartDetail.setSlug(product.getSlug());
		return cartDetail;
	}

	@Transactional
	@Override
	public void insert(CartDto cart, Users user, String address, String phone) throws Exception {
		Orders order = new Orders();
		order.setAddress(address);
		order.setPhone(phone);
		order.setUser(user);
		
		try {
			Orders orderResponse = orderService.insert(order);
			
			for (CartDetailDto cartDetail : cart.getListDetail().values()) {
				// insert ORDER_DETAILS:
				cartDetail.setOrderId(orderResponse.getId());
				orderDetailService.insert(cartDetail);
				
				// update quantity for PRODUCTS
				Products product = productService.findById(cartDetail.getProductId());
				Integer newQuantity = product.getQuantity() - cartDetail.getQuantity();
				productService.updateQuantity(newQuantity, product.getId());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("cannot insert cart to DB");
		}
	}
}
