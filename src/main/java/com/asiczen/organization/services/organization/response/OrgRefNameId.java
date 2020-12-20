package com.asiczen.organization.services.organization.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrgRefNameId {

    private String orgRefName;
    private long orgId;
}
