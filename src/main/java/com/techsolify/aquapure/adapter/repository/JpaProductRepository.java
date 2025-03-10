package com.techsolify.aquapure.adapter.repository;

import com.techsolify.aquapure.adapter.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, Long> {}
