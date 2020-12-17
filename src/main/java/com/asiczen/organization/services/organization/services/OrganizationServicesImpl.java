package com.asiczen.organization.services.organization.services;

import com.asiczen.organization.services.organization.exception.AccessisDeniedException;
import com.asiczen.organization.services.organization.exception.InternalServerError;
import com.asiczen.organization.services.organization.exception.ResourceAlreadyExistException;
import com.asiczen.organization.services.organization.exception.ResourceNotFoundException;
import com.asiczen.organization.services.organization.model.OrgParameters;
import com.asiczen.organization.services.organization.model.Organization;
import com.asiczen.organization.services.organization.repository.OrgParamsRepository;
import com.asiczen.organization.services.organization.repository.OrganizationRepository;
import com.asiczen.organization.services.organization.request.OrganizationOnBoard;
import com.asiczen.organization.services.organization.request.OrganizationUpdateRequest;
import com.asiczen.organization.services.organization.response.DeleteResponse;
import com.asiczen.organization.services.organization.response.OrganizationResponse;
import com.asiczen.organization.services.organization.response.UpdateOrganizationResponse;
import com.asiczen.organization.services.organization.svcimpl.OrganizationServices;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OrganizationServicesImpl implements OrganizationServices {

    @Value("${BASEURL}")
    private String baseUrl;

    @Autowired
    OrganizationRepository orgRepo;

    @Autowired
    OrgParamsRepository paramRepo;

    @Autowired
    RestTemplate restTemplate;

    @Transactional
    @Override
    public OrganizationResponse postNewOrganization(OrganizationOnBoard request, String token) {

        log.debug("Creating new organization.request details are as follows --> {}", request.toString());
        log.trace("Creating a user in keycloak with attributes firstname,lastname,email as userid and phone number");

        Organization organization = orgRepo.findByorgRefName(request.getOrgRefName())
                .orElseThrow(() -> new ResourceAlreadyExistException("Organization Reference name is already taken: " + request.getOrgRefName()));

        /* Save organization */

        Organization org = new Organization();

        org.setOrgRefName(request.getOrgRefName());
        org.setOrgName(request.getOrgName());
        org.setDescription(request.getDescription());
        org.setStatus(true);
        Organization savedOrg = orgRepo.saveAndFlush(org);

        OrgParameters params = new OrgParameters();
        params.setOrganization(savedOrg);
        paramRepo.save(params);

        return new OrganizationResponse("Organization registered successfully and Initial user is created.");

    }

    public void createUserInKeyCloak(@org.jetbrains.annotations.NotNull OrganizationOnBoard request, String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", token);

        log.debug("token --> {}", token);

        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put("userName", request.getContactEmail());
        requestBody.put("firstName", request.getFirstName());
        requestBody.put("lastName", request.getLastName());
        requestBody.put("orgRefName", request.getOrgRefName());
        requestBody.put("contactNumber", request.getContactNumber());

        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response;

        try {
            response = restTemplate.postForEntity(baseUrl + "/createuser", requestEntity, String.class);
        } catch (HttpClientErrorException.Conflict cep) {
            log.error("User already present in the system with {} user name", request.getContactEmail());
            log.error(cep.getMessage());
            throw new ResourceAlreadyExistException("user name already registered in system " + request.getContactEmail());

        } catch (HttpClientErrorException.Forbidden ep) {
            log.error("Resource is forbidden for user {}", request.getContactEmail());
            throw new AccessisDeniedException("Access is denied");
        } catch (HttpClientErrorException.Unauthorized ep) {
            throw new AccessisDeniedException("Access is denied");
        } catch (Exception ep) {
            throw new InternalServerError(ep.getLocalizedMessage());
        }

        if (response.getStatusCodeValue() == 201) {
            log.debug("User was created successfully in keycloak");
        } else if (response.getStatusCodeValue() == 409) {
            log.debug("user already registered in keyCloak");
        }
    }

    @Override
    public List<Organization> getAllOrgnizations() {
        return orgRepo.findAll().stream().sorted(Comparator.comparing(Organization::getUpdatedAt))
                .collect(Collectors.toList());
    }

    @Override
    public UpdateOrganizationResponse updateOrganization(OrganizationUpdateRequest request) {

        /* Check if supplied organization reference name is already there */
        Optional<Organization> checkOrganization = orgRepo.findByorgRefNameAndOrgIdNot(request.getOrgRefName(), request.getOrgId());

        if (checkOrganization.isPresent()) {
            log.trace("Org reference name is already in use.Plase choose a new id");
            throw new ResourceAlreadyExistException("Org Reference name is already taken" + request.getOrgRefName());
        }

        log.trace("Updating Organization object");

        Optional<Organization> org = orgRepo.findByorgId(request.getOrgId());

        if (org.isPresent()) {

            Organization updateOrg = org.get();
            updateOrg.setOrgRefName(request.getOrgRefName());
            updateOrg.setOrgName(request.getOrgName());
            updateOrg.setDescription(request.getDescription());
            updateOrg.setUpdatedAt(new Date());
            orgRepo.save(updateOrg);
            return new UpdateOrganizationResponse("Organization updated successfully.");

        } else {

            log.error("Organization not found by supplied id {} ", request.getOrgId());
            throw new ResourceNotFoundException("Organization is not found by supplied id");
        }

    }

    @Override
    public long getOrganizationCount() {
        return orgRepo.count();
    }

    @Override
    public Organization getOrganizationById(Long orgId) {
        return orgRepo.findByorgId(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization doesn't exist with orgId " + orgId));

    }

    @Override
    public Organization getOrgByRefName(String orgRefName) {
        return orgRepo.findByorgRefName(orgRefName)
                .orElseThrow(() -> new ResourceNotFoundException("Organization doesn't exist with organization reference name"));
    }

    @Override
    public String validateOrganizationReferenceName(String orgReferenceName) {
        if (orgRepo.existsByorgRefName(orgReferenceName)) {
            log.trace("Org reference name is already in use.Plase choose a new id");
            throw new ResourceAlreadyExistException("Org reference name is already in use.Please choose a new id");
        } else {
            log.trace("Org reference name is available.");
            return "Org reference name is available.";
        }
    }

    @Override
    public Organization disableOrganization(Long orgId, boolean status) {
        Optional<Organization> checkOrganization = orgRepo.findByorgId(orgId);

        if (checkOrganization.isPresent()) {

            Organization org = checkOrganization.get();
            org.setStatus(status);
            orgRepo.save(org);
            return org;
        } else {
            throw new ResourceNotFoundException("Organization not found by supplied id");
        }
    }

    @Override
    public DeleteResponse deleteOrganizationByOrgId(Long orgId) {
        orgRepo.findByorgId(orgId).ifPresentOrElse(organization -> orgRepo.delete(organization), () -> new ResourceNotFoundException("Invalid organization id."));
        return new DeleteResponse("Organization deleted successfully.");
    }
}
