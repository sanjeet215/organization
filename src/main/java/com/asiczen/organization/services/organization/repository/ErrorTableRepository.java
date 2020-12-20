package com.asiczen.organization.services.organization.repository;

import com.asiczen.organization.services.organization.model.ErrorTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorTableRepository extends JpaRepository<ErrorTable, Long> {

}
