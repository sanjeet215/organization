package com.asiczen.organization.services.organization.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrgLevelParams {

	@NotEmpty(message = "Organization unique name is required/Can't be blank")
	@Pattern(regexp = "\\s*\\S+\\s*", message = "Can't contain space in organization reference name")
	@Size(min = 3, max = 10, message = "orgName should be between 3 to 10 characters")
	private String orgid;
	
	@Min(value = 0L)
	@Max(value = 100L)
	private Integer overSpeedLimit = 80;
	
	@Min(value = 0L)
	@Max(value = 100L)
	private Integer underSpeedLimit = 20;
	private Integer fuelLimit = 10;
	private Integer underUtilizedHours = 2;
	private Integer overUtilizedHours = 12;
	private Integer underUtilizedKms = 50;
	private Integer overUtilizedKms = 500;
}
