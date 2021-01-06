package com.asiczen.organization.services.organization.services;


import com.asiczen.organization.services.organization.exception.ResourceNotFoundException;
import com.asiczen.organization.services.organization.model.OrgParameters;
import com.asiczen.organization.services.organization.repository.OrgParamsRepository;
import com.asiczen.organization.services.organization.request.OrgParamCreateRequest;
import com.asiczen.organization.services.organization.request.OrgParamUpdateRequest;
import com.asiczen.organization.services.organization.response.OrganizationPraramSaveUpdResponse;
import com.asiczen.organization.services.organization.svcimpl.OrgParamServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class OrgParamServicesImpl implements OrgParamServices {

    @Autowired
    OrgParamsRepository orgParamRepo;

    @Override
    public OrganizationPraramSaveUpdResponse saveOrganizationParameters(OrgParamCreateRequest request) {

        OrgParameters orgParameters = new OrgParameters();

        orgParamRepo.findByparamId(request.getOrgId()).orElseThrow(() -> new ResourceNotFoundException("Invalid Organization id, please choose a correct id."));

        if (request.getOrgId() != null) {
            BeanUtils.copyProperties(request, orgParameters);
            orgParameters.setParamId(request.getOrgId());

            orgParamRepo.save(orgParameters);
        }

        return new OrganizationPraramSaveUpdResponse("Org Level parameters are saved successfully.");
    }

    @Override
    public OrganizationPraramSaveUpdResponse updateOrganizationParameters(OrgParamUpdateRequest request) {

        if (request != null) {
            Optional<OrgParameters> orgParams = orgParamRepo.findById(request.getOrgid());
            if (orgParams.isPresent()) {
                updateOrganizationParams(request, orgParams.get());
                orgParamRepo.save(updateOrganizationParams(request, orgParams.get()));
                return new OrganizationPraramSaveUpdResponse("Organization parameters updated successfully.");
            } else {
                throw new ResourceNotFoundException("incorrect id , please rectify the id");
            }
        } else {
            throw new ResourceNotFoundException("incorrect id , please rectify the id");
        }

    }

    private OrgParameters updateOrganizationParams(OrgParamUpdateRequest request, OrgParameters updateObject) {

        if (request.getOverSpeedLimit() > 0) {
            updateObject.setOverSpeedLimit(request.getOverSpeedLimit());
        }

        if (request.getFuelLimit() > 0) {
            updateObject.setFuelLimit(request.getFuelLimit());
        }

        if (request.getUnderSpeedLimit() > 0) {
            updateObject.setUnderSpeedLimit(request.getUnderSpeedLimit());
        }

        if (request.getOverUtilizedHours() > 0) {
            updateObject.setOverUtilizedHours(request.getOverUtilizedHours());
        }

        if (request.getUnderUtilizedHours() > 0) {
            updateObject.setUnderUtilizedHours(request.getUnderUtilizedHours());
        }

        if (request.getOverUtilizedKms() > 0) {
            updateObject.setOverUtilizedKms(request.getOverUtilizedKms());
        }

        if (request.getUnderUtilizedKms() > 0) {
            updateObject.setUnderUtilizedKms(request.getUnderUtilizedKms());
        }

        return updateObject;
    }

    @Override
    public OrgParameters getMyOrganizationParameters(Long orgid) {
        Optional<OrgParameters> response = orgParamRepo.findByparamId(orgid);

        if (response.isEmpty()) {
            throw new ResourceNotFoundException("invalid Organization id: " + orgid);
        } else {
            OrgParameters returnResponse = new OrgParameters();
            BeanUtils.copyProperties(response, returnResponse);

            return returnResponse;
        }
    }

}
