package com.asiczen.organization.services.organization.services;

import com.asiczen.organization.services.organization.dto.CSVData;
import com.asiczen.organization.services.organization.exception.InternalServerError;
import com.asiczen.organization.services.organization.model.ErrorTable;
import com.asiczen.organization.services.organization.model.OrgParameters;
import com.asiczen.organization.services.organization.model.Organization;
import com.asiczen.organization.services.organization.repository.ErrorTableRepository;
import com.asiczen.organization.services.organization.repository.OrgParamsRepository;
import com.asiczen.organization.services.organization.repository.OrganizationRepository;
import com.asiczen.organization.services.organization.request.OrgParamUpdateRequest;
import com.asiczen.organization.services.organization.svcimpl.CsvFileServices;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    ErrorTableRepository errorTableRepository;

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

    @Override
    public ByteArrayInputStream downloadOrganizationData(String token) {

        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);) {
            String header = "orgRefName,orgName,Description,status,overSpeedLimit,underSpeedLimit,fuelLimit,underUtilizedHours,overUtilizedHours,underUtilizedKms,overUtilizedKms";
            csvPrinter.printRecords(header);
            organizationRepository.findAll().stream().map(record -> convertToData(record)).forEach(record -> writeToFile(csvPrinter, record));
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalServerError("Some IO error while downloading the file.");
        } catch (Exception e) {
            throw new InternalServerError("Some error while downloading the file.");
        }
    }

    private void writeToFile(CSVPrinter csvPrinter, CSVData record) {

        try {
            String data = record.getOrgName() + "," + record.getOrgName() + "," + record.getDescription() + "," + record.isStatus() + "," + record.getOverSpeedLimit() + "," + record.getUnderSpeedLimit() + "," + record.getFuelLimit() + "," +
                    record.getOverUtilizedHours() + "," + record.getUnderUtilizedHours() + "," + record.getUnderUtilizedKms() + "," + record.getOverUtilizedKms();
            csvPrinter.printRecords(data);
        } catch (Exception exception) {
            log.error("Not able to write data into file.");
        }
    }


    private CSVData convertToData(Organization organization) {

        CSVData data = new CSVData();
        BeanUtils.copyProperties(organization, data);
        return data;
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
                generateErrorRecord(csvRecord, true, "Record is good.");
            } catch (Exception exception) {
                log.error("Error while updating the record -> " + exception.getLocalizedMessage());
                generateErrorRecord(csvRecord, false, exception.getLocalizedMessage());
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
                generateErrorRecord(csvRecord, true, "Record is good.");
                log.trace("Organization record saved successfully.");

            } catch (Exception ep) {
                log.error("Error while creating new data from file => " + ep.getLocalizedMessage());
                log.error(ep.getLocalizedMessage());
                ep.getStackTrace();
                generateErrorRecord(csvRecord, false, ep.getLocalizedMessage());
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

        if (csvRecord.get("orgRefName") != null) {
            data.setOrgRefName(csvRecord.get("orgRefName"));
        } else {
            //Failure logic
            generateErrorRecord(data, false, "Organization Reference Name can't be blank,please populate.");
        }
        return data;
    }

    private void generateErrorRecord(CSVData data, boolean status, String message) {
        String actualRecord = getActualString(data.toString());
        String errorMessage = getActualString(message);
        ErrorTable error = new ErrorTable(actualRecord, LocalDateTime.now(), false, errorMessage);
        errorTableRepository.save(error);
    }

    private String getActualString(String inputString) {
        String response = "messaage";
        int length = inputString.length();
        if (length > 250) {
            response = inputString.substring(0, 250);
        } else {
            response = inputString;
        }
        return response;
    }

}