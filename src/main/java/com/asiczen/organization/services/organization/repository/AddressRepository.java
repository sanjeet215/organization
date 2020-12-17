package com.asiczen.organization.services.organization.repository;

import com.asiczen.organization.services.organization.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

	Optional<Address> findByaddressId(Long addressId);
}
