package com.mockproject.api;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mockproject.constant.SessionConst;
import com.mockproject.dto.CartDto;
import com.mockproject.entity.Users;
import com.mockproject.service.CartService;
import com.mockproject.util.SessionUtil;

@RestController
@RequestMapping("/api/cart")
public class CartApi {
	
	@Autowired
	private CartService cartService;
	
	// localhost:8080/api/cart/update?productId={...}&quantity={...}&isReplace={...}
	@GetMapping("/update")
	public ResponseEntity<?> doGetUpdate(@RequestParam("productId") Long productId,
			@RequestParam("quantity") Integer quantity,
			@RequestParam("isReplace") Boolean isReplace,
			HttpSession session) {
		CartDto currentCart = SessionUtil.getCurrentCart(session);
		cartService.updateCart(currentCart, productId, quantity, isReplace);
		session.setAttribute(SessionConst.CURRENT_CART, currentCart);
		return ResponseEntity.ok(currentCart);
	}
	
	// /api/cart/refresh
	@GetMapping("/refresh")
	public ResponseEntity<?> doGetRefresh(HttpSession session) {
		return ResponseEntity.ok(SessionUtil.getCurrentCart(session));
	}
	
	// /api/cart/checkout?address=...&phone=...
	@GetMapping("/checkout")
	public ResponseEntity<?> doGetCheckOut(
			@RequestParam("address") String address,
			@RequestParam("phone") String phone,
			HttpSession session) {
		Users currentUser = (Users) session.getAttribute(SessionConst.CURRENT_USER);
		
		if (currentUser != null) {
			CartDto currentCart = SessionUtil.getCurrentCart(session);
			try {
				cartService.insert(currentCart, currentUser, address, phone);
				session.setAttribute(SessionConst.CURRENT_CART, new CartDto());
				return new ResponseEntity<>(HttpStatus.OK); // 200
			} catch (Exception ex) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400
			}
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403
		}
	}
}
