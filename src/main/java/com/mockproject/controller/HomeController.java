package com.mockproject.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mockproject.constant.SessionConst;
import com.mockproject.entity.Products;
import com.mockproject.entity.Users;
import com.mockproject.service.ProductsService;
import com.mockproject.service.UsersService;

@Controller
@RequestMapping("/")
public class HomeController {
	
	@Autowired
	private ProductsService productService;
	
	@Autowired
	private UsersService userService;
	
	// localhost:8080/index
	@GetMapping("index")
	public String doGetIndex(Model model) {
		List<Products> products = productService.findAll();
		model.addAttribute("products", products);
		return "user/index";
	}
	
	// localhost:8080/login
	@GetMapping("login")
	public String doGetLogin(Model model) {
		model.addAttribute("userRequest", new Users());
		return "user/login";
	}
	
	// localhost:8080/logout
	@GetMapping("logout")
	public String doGetLogout(HttpSession session) {
		session.removeAttribute(SessionConst.CURRENT_USER);
		return "redirect:/index";
	}
	
	// localhost:8080/register
	@GetMapping("register")
	public String doGetRegister(Model model) {
		model.addAttribute("userRegister", new Users());
		return "user/register";
	}
	
	@PostMapping("login")
	public String doPostLogin(@ModelAttribute("userRequest") Users userRequest, HttpSession session) {
		Users userResponse = userService.doLogin(userRequest.getUsername(), userRequest.getHashPassword());
		if (userResponse != null) {
			session.setAttribute(SessionConst.CURRENT_USER, userResponse);
			return "redirect:/index";
		} else {
			return "redirect:/login";
		}
	}
	
	@PostMapping("register")
	public String doPostRegister(@ModelAttribute("userRegister") Users userRegister, HttpSession session) {
		Users userResponse = userService.save(userRegister);
		if (userResponse != null) {
			session.setAttribute(SessionConst.CURRENT_USER, userResponse);
			return "redirect:/index";
		} else {
			return "redirect:/register";
		}
	}
}
