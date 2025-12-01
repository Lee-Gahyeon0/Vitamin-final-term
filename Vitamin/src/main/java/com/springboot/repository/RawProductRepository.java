package com.springboot.repository;

import com.springboot.domain.RawProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawProductRepository
extends JpaRepository<RawProduct, Long> {
}
