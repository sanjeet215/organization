package com.asiczen.organization.services.organization.svcimpl;


import com.asiczen.organization.services.organization.model.OrgParameters;
import com.asiczen.organization.services.organization.request.OrgParamCreateRequest;
import com.asiczen.organization.services.organization.request.OrgParamUpdateRequest;
import com.asiczen.organization.services.organization.response.OrganizationPraramSaveUpdResponse;
import org.springframework.stereotype.Service;

@Service
public interface OrgParamServices {

	public OrganizationPraramSaveUpdResponse saveOrganizationParameters(OrgParamCreateRequest request);

	public OrganizationPraramSaveUpdResponse updateOrganizationParameters(OrgParamUpdateRequest request);

	public OrgParameters getMyOrganizationParameters(Long orgid);
}
