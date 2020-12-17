package com.asiczen.organization.services.organization.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Person {

	@NotEmpty(message = "First Name is required/Can't be blank")
	@Size(min = 1, max = 16, message = "First Name should be between 1 to 16 characters")
	public String firstName;

	@NotEmpty(message = "Last Name is required/Can't be blank")
	@Size(min = 1, max = 16, message = "Last Name should be between 1 to 16 characters")
	private String lastName;

	@NotEmpty(message = "contact Number is required/Can't be blank")
	private String contactNumber;

	@Email
	private String contactEmail;
}
