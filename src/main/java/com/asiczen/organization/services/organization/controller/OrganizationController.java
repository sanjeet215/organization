package com.asiczen.organization.services.organization.controller;


import com.asiczen.organization.services.organization.exception.FileUploadException;
import com.asiczen.organization.services.organization.model.ErrorTable;
import com.asiczen.organization.services.organization.model.OrgParameters;
import com.asiczen.organization.services.organization.model.Organization;
import com.asiczen.organization.services.organization.request.OrgParamCreateRequest;
import com.asiczen.organization.services.organization.request.OrgParamUpdateRequest;
import com.asiczen.organization.services.organization.request.OrganizationOnBoard;
import com.asiczen.organization.services.organization.request.OrganizationUpdateRequest;
import com.asiczen.organization.services.organization.response.*;
import com.asiczen.organization.services.organization.svcimpl.CsvFileServices;
import com.asiczen.organization.services.organization.svcimpl.ErrorTableServices;
import com.asiczen.organization.services.organization.svcimpl.OrgParamServices;
import com.asiczen.organization.services.organization.svcimpl.OrganizationServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.service.ResponseMessage;

import javax.validation.Valid;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/service")
@Slf4j
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class OrganizationController {

    @Autowired
    OrganizationServices orgService;

    @Autowired
    OrgParamServices orgParamServices;

    @Autowired
    CsvFileServices csvFileServices;

    @Autowired
    ErrorTableServices errorTableServices;

    @PostMapping("/org")
    @ResponseStatus(HttpStatus.CREATED)
    public OrganizationResponse createOrganization(@Valid @RequestBody OrganizationOnBoard request, @RequestHeader String authorization) {

        log.trace("Create organization method is invoked. --> {} ", request.toString());
        return orgService.postNewOrganization(request, authorization);
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
                "Organization List Extracted Successfully", orgService.getAllOrganizations()));
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
    public Organization getOrganizationById(@Valid @PathVariable("orgid") Long orgid) {
        return orgService.getOrganizationById(orgid);
    }

    @GetMapping("/org/orgref/{orgRefName}")
    @ResponseStatus(HttpStatus.OK)
    public Organization getOrganizationByOrgReferenceName(@Valid @PathVariable("orgRefName") String orgRefName) {
        return orgService.getOrgByRefName(orgRefName);
    }


    @GetMapping("/org/count")
    @ResponseStatus(HttpStatus.OK)
    public CountResponse getOrganizationCount() {
        return new CountResponse(orgService.getOrganizationCount(), "count extracted successfully.");
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
    public OrgParameters getMyOrganizationParametersById(@Valid @PathVariable("orgid") Long orgid) {
        log.info("Received --------------------------------------->: {}", orgid);
        return orgParamServices.getMyOrganizationParameters(orgid);
    }

    @DeleteMapping("/org/{orgid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public DeleteResponse deleteOrganizationById(@Valid @PathVariable("orgid") Long orgid) {
        return orgService.deleteOrganizationByOrgId(orgid);
    }

    @PostMapping("/org/upload")
    @ResponseStatus(HttpStatus.OK)
    public FileUploadResponse uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader String authorization) {

        if (csvFileServices.isCSVFormattedFile(file)) {
            throw new FileUploadException("Invalid file format. please check the format");
        }
        csvFileServices.uploadOrganizationData(file);

        return new FileUploadResponse("File uploaded successfully.");
    }


    @GetMapping("/org/download")
    public ResponseEntity<Resource> downloadFile(@RequestHeader String authorization) {

        String filename = "organization.csv";

        InputStreamResource file = new InputStreamResource(csvFileServices.downloadOrganizationData(authorization));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    @GetMapping("/org/orgrefname")
    public ResponseEntity<ApiResponse> getListOfOrganizationReferenceNames(@RequestHeader String authorization) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(HttpStatus.OK.value(), "List extracted.", orgService.getAllOrganizationRefName(authorization)));
    }

    @GetMapping("/org/error")
    public ResponseEntity<ApiResponse> getErrorListForOrganizationFileUpload(@RequestHeader String authorization) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(HttpStatus.OK.value(), "Errors extracted.", errorTableServices.getAllErrorDetails()));
    }

    @GetMapping("/org/errorflag")
    public ResponseEntity<ApiResponse> getErrorListForOrganizationFileUpload(@Valid @RequestParam boolean status, @RequestHeader String authorization) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(HttpStatus.OK.value(), "Errors extracted.", errorTableServices.getAllErrorDetailsWithFlag(status)));
    }
}
