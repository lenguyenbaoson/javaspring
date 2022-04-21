package com.mockproject.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mockproject.constant.SessionConst;
import com.mockproject.dto.CartDto;
import com.mockproject.service.CartService;

@Controller
@RequestMapping("/cart")
public class CartController {
	
	@Autowired
	private CartService cartService;

	@GetMapping("")
	public String doGetViewCart(HttpSession session, Model model) {
		return "user/cart";
	}
	
	// localhost:8080/cart/update?productId={...}&quantity={...}&isReplace={...}
	@GetMapping("/update")
	public String doGetUpdate(@RequestParam("productId") Long productId,
			@RequestParam("quantity") Integer quantity,
			@RequestParam("isReplace") Boolean isReplace,
			HttpSession session) {
		CartDto currentCart = (CartDto) session.getAttribute(SessionConst.CURRENT_CART);
		cartService.updateCart(currentCart, productId, quantity, isReplace);
		session.setAttribute(SessionConst.CURRENT_CART, currentCart);
		return "user/cart::#viewCartFragment";
	}
}
