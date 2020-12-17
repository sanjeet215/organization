package com.asiczen.organization.services.organization.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrgParamUpdateRequest {

    @NotNull(message = "Organization id is a required field can't be left blank")
    private Long orgid;

    @NotNull(message = "overSpeedLimit is a required field can't be left blank")
    private Integer overSpeedLimit = 0;

    private Integer underSpeedLimit = 0;
    private Integer fuelLimit = 0;
    private Integer underUtilizedHours = 0;
    private Integer overUtilizedHours = 0;
    private Integer underUtilizedKms = 0;
    private Integer overUtilizedKms = 0;
}
