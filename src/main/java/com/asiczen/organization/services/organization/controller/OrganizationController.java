package com.asiczen.organization.services.organization.controller;


import com.asiczen.organization.services.organization.model.OrgParameters;
import com.asiczen.organization.services.organization.model.Organization;
import com.asiczen.organization.services.organization.request.OrgParamCreateRequest;
import com.asiczen.organization.services.organization.request.OrgParamUpdateRequest;
import com.asiczen.organization.services.organization.request.OrganizationOnBoard;
import com.asiczen.organization.services.organization.request.OrganizationUpdateRequest;
import com.asiczen.organization.services.organization.response.*;
import com.asiczen.organization.services.organization.svcimpl.OrgParamServices;
import com.asiczen.organization.services.organization.svcimpl.OrganizationServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/fleet")
@Slf4j
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class OrganizationController {

    @Autowired
    OrganizationServices orgService;

    @Autowired
    OrgParamServices orgParamServices;

    @PostMapping("/org")
    @ResponseStatus(HttpStatus.CREATED)
    public OrganizationResponse createOrganization(@Valid @RequestBody OrganizationOnBoard request,
                                                   @RequestHeader String Authorization) {

        log.trace("Create organization method is invoked. --> {} ", request.toString());
        return orgService.postNewOrganization(request, Authorization);
    }

    @PutMapping("/org")
    @ResponseStatus(HttpStatus.OK)
    public UpdateOrganizationResponse updateOrganization(@Valid @RequestBody OrganizationUpdateRequest updateRequest) {

        log.debug("Updating organization, parameters are --> {} ", updateRequest.toString());
        return orgService.updateOrganization(updateRequest);

    }

    @GetMapping("/org")
    public ResponseEntity<ApiResponse> getAllOrganizations() {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(),
                "Organization List Extracted Successfully", orgService.getAllOrgnizations()));
    }

    @GetMapping("/org/validate")
    public ResponseEntity<ApiResponse> validateOrganization(@Valid @RequestParam String orgReferenceName) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(HttpStatus.OK.value(), "Organization " + orgReferenceName + " valiated",
                        orgService.validateOrganizationReferenceName(orgReferenceName.trim())));
    }

    @PutMapping("/org/disable")
    public ResponseEntity<ApiResponse> updateStatus(@Valid @RequestParam boolean status, @Valid @RequestParam Long orgId) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(HttpStatus.OK.value(),
                "Status of organization: " + orgId + " is updated", orgService.disableOrganization(orgId, status)));
    }

    @GetMapping("/org/{orgid}")
    @ResponseStatus(HttpStatus.OK)
    public Organization getOrganizationById(@Valid @PathVariable Long orgid) {
        return orgService.getOrganizationById(orgid);
    }

    @GetMapping("/org/count")
    @ResponseStatus(HttpStatus.OK)
    public CountResponse getOrganizationCount() {
        return new CountResponse(orgService.getOrganizationCount(),"count extracted successfully.");
    }

    @PostMapping("/org/param")
    @ResponseStatus(HttpStatus.CREATED)
    public OrganizationPraramSaveUpdResponse postOrganizationParameters(@Valid @RequestBody OrgParamCreateRequest request) {
        return orgParamServices.saveOrganizationParameters(request);
    }

    @PutMapping("/org/param")
    @ResponseStatus(HttpStatus.OK)
    public OrganizationPraramSaveUpdResponse updateOrganizationParameters(@Valid @RequestBody OrgParamUpdateRequest request) {
        log.error("Request --> {}", request.getOrgid());
        return orgParamServices.updateOrganizationParameters(request);
    }

    @GetMapping("/org/param/{orgid}")
    @ResponseStatus(HttpStatus.OK)
    public OrgParameters getMyOrganizationParametersById(@Valid @PathVariable Long orgid) {
        log.info("Received --------------------------------------->: {}", orgid);
        return orgParamServices.getMyOrganizationParameters(orgid);
    }

}
