package com.asiczen.organization.services.organization.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "address")
public class Address implements Serializable {

	private static final long serialVersionUID = -1150443710360973399L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long addressId;

	private String addressLine1;

	private String addressLine2;

	private String street;

	private String city;

	private String state;

	private String country;

	private String zipCode;

}
