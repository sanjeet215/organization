package com.asiczen.organization.services.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CSVData {

    private String orgRefName;
    private String orgName;
    private String description;
    private boolean status;

    private int overSpeedLimit = 0;
    private int underSpeedLimit = 0;
    private int fuelLimit = 0;
    private int underUtilizedHours = 0;
    private int overUtilizedHours = 0;
    private int underUtilizedKms = 0;
    private int overUtilizedKms = 0;

}
