package com.asiczen.organization.services.organization.repository;


import com.asiczen.organization.services.organization.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByorgRefName(String orgRefName);

    Optional<Organization> findByorgId(Long orgId);

    boolean existsByorgRefName(String orgRefName);

    Long countByStatus(boolean status);

    Optional<Organization> findByorgRefNameAndOrgIdNot(String orgRefName, Long orgId);


}
