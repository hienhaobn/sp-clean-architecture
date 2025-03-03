package com.techsolify.aquapure.adapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techsolify.aquapure.adapter.entity.ProductEntity;

@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, Long> {
}
