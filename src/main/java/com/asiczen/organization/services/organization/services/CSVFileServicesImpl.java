package com.asiczen.organization.services.organization.services;

import com.asiczen.organization.services.organization.dto.CSVData;
import com.asiczen.organization.services.organization.model.OrgParameters;
import com.asiczen.organization.services.organization.model.Organization;
import com.asiczen.organization.services.organization.repository.OrgParamsRepository;
import com.asiczen.organization.services.organization.repository.OrganizationRepository;
import com.asiczen.organization.services.organization.request.OrgParamUpdateRequest;
import com.asiczen.organization.services.organization.svcimpl.CsvFileServices;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class CSVFileServicesImpl implements CsvFileServices {

    private static final String TYPE = "text/csv";

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    OrgParamsRepository paramsRepository;

    @Autowired
    OrgParamServicesImpl orgParamServices;

    @Override
    public boolean isCSVFormattedFile(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    @Override
    public void uploadOrganizationData(MultipartFile file) {

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

            csvParser.getRecords().parallelStream().map(record -> parseCSVData(record)).forEach(processedRecord -> saveToDatabase(processedRecord));

        } catch (IOException exception) {
            log.error("Error while reading the file");
            log.error(exception.getLocalizedMessage());

        } catch (Exception ep) {
            log.error("Error while reading the file");
            log.error(ep.getLocalizedMessage());
        }
    }

    void saveToDatabase(CSVData csvRecord) {

        Optional<Organization> organization = organizationRepository.findByorgRefName(csvRecord.getOrgRefName());
        if (organization.isPresent()) {
            log.trace("Record is already there, so updating the record {} ", organization);
            try {
                organizationRepository.save(setOrganizationFields(organization.get(), csvRecord));
                OrgParamUpdateRequest orgParamUpdateRequest = new OrgParamUpdateRequest();
                setOrganizationParametersUpdateRequest(orgParamUpdateRequest, csvRecord, organization.get().getOrgId());
                orgParamServices.updateOrganizationParameters(orgParamUpdateRequest);
            } catch (Exception exception) {
                log.error("Error while updating the record -> " + exception.getLocalizedMessage());
            }
        } else {
            try {
                log.trace("Record is new, so creating new record in DB");
                Organization newOrganization = new Organization();
                setOrganizationFields(newOrganization, csvRecord);
                newOrganization.setOrgRefName(csvRecord.getOrgRefName());
                OrgParameters newOrgParameters = new OrgParameters();
                newOrgParameterSetter(newOrgParameters, csvRecord);
                newOrgParameters.setOrganization(newOrganization);

                log.trace("saving record {} ", newOrganization.toString());

                paramsRepository.save(newOrgParameters);

                log.trace("Organization record saved successfully.");

            } catch (Exception ep) {
                log.error("Error while creating new data from file => " + ep.getLocalizedMessage());
                log.error(ep.getLocalizedMessage());
                ep.getStackTrace();
            }
        }


    }

    private Organization setOrganizationFields(Organization organization, CSVData csvRecord) {
        //organization.setOrgName(csvRecord.get("orgRefName"));
        organization.setDescription(csvRecord.getDescription());
        organization.setStatus(csvRecord.isStatus());
        organization.setOrgName(csvRecord.getOrgName());
        organization.setUpdatedAt(new Date());
        return organization;
    }

    private OrgParamUpdateRequest setOrganizationParametersUpdateRequest(OrgParamUpdateRequest orgParamUpdateRequest, CSVData csvRecord, Long orgId) {
        orgParamUpdateRequest.setOrgid(orgId);
        orgParamUpdateRequest.setOverSpeedLimit(csvRecord.getOverSpeedLimit());
        orgParamUpdateRequest.setUnderSpeedLimit(csvRecord.getUnderSpeedLimit());
        orgParamUpdateRequest.setFuelLimit(csvRecord.getFuelLimit());
        orgParamUpdateRequest.setUnderUtilizedHours(csvRecord.getUnderUtilizedHours());
        orgParamUpdateRequest.setOverUtilizedHours(csvRecord.getOverUtilizedHours());
        orgParamUpdateRequest.setUnderUtilizedKms(csvRecord.getUnderUtilizedKms());
        orgParamUpdateRequest.setOverUtilizedKms(csvRecord.getOverUtilizedKms());
        return orgParamUpdateRequest;
    }

    public void newOrgParameterSetter(OrgParameters newOrgParameters, CSVData csvRecord) {

        newOrgParameters.setOverSpeedLimit(csvRecord.getOverSpeedLimit());
        newOrgParameters.setUnderSpeedLimit(csvRecord.getUnderSpeedLimit());
        newOrgParameters.setFuelLimit(csvRecord.getFuelLimit());
        newOrgParameters.setUnderUtilizedHours(csvRecord.getUnderUtilizedHours());
        newOrgParameters.setOverUtilizedHours(csvRecord.getOverUtilizedHours());
        newOrgParameters.setUnderUtilizedKms(csvRecord.getUnderUtilizedKms());
        newOrgParameters.setOverUtilizedKms(csvRecord.getOverUtilizedKms());
    }

    private CSVData parseCSVData(CSVRecord csvRecord) {
        CSVData data = new CSVData();

        if (csvRecord.get("overSpeedLimit") != null) {
            data.setOverSpeedLimit(Integer.parseInt(csvRecord.get("overSpeedLimit")));
        } else {
            data.setOverSpeedLimit(0);
        }

        if (csvRecord.get("underSpeedLimit") != null) {
            data.setUnderSpeedLimit(Integer.parseInt(csvRecord.get("underSpeedLimit")));
        } else {
            data.setUnderSpeedLimit(0);
        }

        if (csvRecord.get("fuelLimit") != null) {
            data.setFuelLimit(Integer.parseInt(csvRecord.get("fuelLimit")));
        } else {
            data.setFuelLimit(0);
        }
        if (csvRecord.get("underUtilizedHours") != null) {
            data.setUnderUtilizedHours(Integer.parseInt(csvRecord.get("underUtilizedHours")));
        } else {
            data.setUnderUtilizedHours(0);
        }
        if (csvRecord.get("overUtilizedHours") != null) {
            data.setOverUtilizedHours(Integer.parseInt(csvRecord.get("overUtilizedHours")));
        } else {
            data.setOverUtilizedHours(0);
        }

        if (csvRecord.get("underUtilizedKms") != null) {
            data.setUnderUtilizedKms(Integer.parseInt(csvRecord.get("underUtilizedKms")));
        } else {
            data.setUnderUtilizedKms(0);
        }
        if (csvRecord.get("overUtilizedKms") != null) {
            data.setOverUtilizedKms(Integer.parseInt(csvRecord.get("overUtilizedKms")));
        } else {
            data.setOverUtilizedKms(0);
        }

        if (csvRecord.get("orgRefName") != null) {
            data.setOrgRefName(csvRecord.get("orgRefName"));
        } else {
            //Failure logic
        }

        if (csvRecord.get("orgName") != null) {
            data.setOrgName(csvRecord.get("orgName"));
        } else {
            data.setOrgName("Not Available");
        }

        if (csvRecord.get("Description") != null) {
            data.setDescription(csvRecord.get("Description"));
        } else {
            data.setDescription("Not Available");
        }

        if (csvRecord.get("status") != null) {
            data.setStatus(Boolean.parseBoolean(csvRecord.get("status")));
        } else {
            data.setStatus(false);
        }

        return data;
    }


}