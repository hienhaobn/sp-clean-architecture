package com.techsolify.aquapure.adapter.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.techsolify.aquapure.adapter.entity.ProductEntity;
import com.techsolify.aquapure.adapter.mapper.ProductEntityMapper;
import com.techsolify.aquapure.domain.entity.Product;
import com.techsolify.aquapure.domain.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final JpaProductRepository jpaProductRepository;
    private final ProductEntityMapper productEntityMapper;
    
    @Override
    public List<Product> findAll() {
        return jpaProductRepository.findAll().stream()
                .map(productEntityMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Product> findById(Long id) {
        return jpaProductRepository.findById(id)
                .map(productEntityMapper::toDomain);
    }
    
    @Override
    public Product save(Product product) {
        ProductEntity entity = productEntityMapper.toEntity(product);
        ProductEntity savedEntity = jpaProductRepository.save(entity);
        return productEntityMapper.toDomain(savedEntity);
    }
    
    @Override
    public void deleteById(Long id) {
        jpaProductRepository.deleteById(id);
    }
    
}
