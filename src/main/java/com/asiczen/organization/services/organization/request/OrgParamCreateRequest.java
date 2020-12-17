package com.asiczen.organization.services.organization.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class OrgParamCreateRequest {

	@NotNull(message = "Organization id is a required field can't be left blank")
	private Long orgId;

	private Integer overSpeedLimit = 80;
	private Integer underSpeedLimit = 20;
	private Integer fuelLimit = 10;
	private Integer underUtilizedHours = 2;
	private Integer overUtilizedHours = 12;
	private Integer underUtilizedKms = 50;
	private Integer overUtilizedKms = 500;
}
