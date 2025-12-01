package com.springboot.repository;

import com.springboot.domain.RawIndividualIngredient;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RawIndividualIngredientRepository
extends JpaRepository<RawIndividualIngredient, Long> {
}