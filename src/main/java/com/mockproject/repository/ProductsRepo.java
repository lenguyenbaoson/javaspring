package com.mockproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mockproject.entity.Products;

@Repository
public interface ProductsRepo extends JpaRepository<Products, Long> {
	
	@Query(value = "SELECT * FROM products WHERE isDeleted = 0 AND quantity > 0", 
			nativeQuery = true)
	List<Products> findAllAvailable();
	
	List<Products> findByIsDeletedFalseAndQuantityGreaterThan(Integer quantity);
	
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE products SET quantity = ? WHERE id = ?", nativeQuery = true)
	void updateQuantity(Integer newQuantity, Long productId);
}
