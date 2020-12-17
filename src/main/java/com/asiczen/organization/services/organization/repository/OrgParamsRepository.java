package com.asiczen.organization.services.organization.repository;

import com.asiczen.organization.services.organization.model.OrgParameters;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrgParamsRepository extends JpaRepository<OrgParameters,Long>{

	Optional<OrgParameters> findByparamId(Long orgid);
}
