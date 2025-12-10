package com.springboot.repository;

import com.springboot.domain.NormalizedSupplement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NormalizedSupplementRepository extends JpaRepository<NormalizedSupplement, Long> {
}
