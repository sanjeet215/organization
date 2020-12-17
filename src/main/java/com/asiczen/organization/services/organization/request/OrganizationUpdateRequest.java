package com.asiczen.organization.services.organization.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class OrganizationUpdateRequest {

	@NotNull(message = "Organization id is a requried field can't be left blank")
	private Long orgId;

	@NotEmpty(message = "Organization unique name is required/Can't be blank")
	@Pattern(regexp = "\\s*\\S+\\s*", message = "Can't contain space in organization reference name")
	@Size(min = 3, max = 10, message = "orgRefName should be between 3 to 10 characters")
	private String orgRefName;

	@NotEmpty(message = "Organization Name is required/Can't be blank")
	@Size(min = 3, max = 50, message = "orgName should be between 3 to 50 characters")
	private String orgName;

	@NotEmpty(message = "Description is required/Can't be blank")
	@Size(min = 1, max = 100, message = "description should be between 1 to 100 characters")
	private String description;

}
