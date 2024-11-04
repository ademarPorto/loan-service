package com.ademarporto.ls.repository;

import com.ademarporto.ls.repository.entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanRepository extends JpaRepository<LoanEntity, UUID> {
}
