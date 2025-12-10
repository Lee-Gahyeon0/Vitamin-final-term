package com.springboot.repository;

import com.springboot.domain.NormalizedSupplement;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface NormalizedSupplementRepository
     extends JpaRepository<NormalizedSupplement, Long> {

 List<NormalizedSupplement> findByProductNameContaining(String keyword);

 List<NormalizedSupplement> findByTagsContaining(String tag);
}
