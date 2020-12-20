package com.asiczen.organization.services.organization.svcimpl;


import com.asiczen.organization.services.organization.model.Organization;
import com.asiczen.organization.services.organization.request.OrganizationOnBoard;
import com.asiczen.organization.services.organization.request.OrganizationUpdateRequest;
import com.asiczen.organization.services.organization.response.DeleteResponse;
import com.asiczen.organization.services.organization.response.OrgRefNameId;
import com.asiczen.organization.services.organization.response.OrganizationResponse;
import com.asiczen.organization.services.organization.response.UpdateOrganizationResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrganizationServices {

    public OrganizationResponse postNewOrganization(OrganizationOnBoard request, String token);

    public List<Organization> getAllOrgnizations();

    public UpdateOrganizationResponse updateOrganization(OrganizationUpdateRequest request);

    public long getOrganizationCount();

    public Organization getOrganizationById(Long orgId);

    public Organization getOrgByRefName(String orgRefName);

    public String validateOrganizationReferenceName(String orgReferenceName);

    public Organization disableOrganization(Long orgId, boolean status);

    public DeleteResponse deleteOrganizationByOrgId(Long orgId);

    public List<OrgRefNameId> getAllOrganizationRefName(String token);
}
