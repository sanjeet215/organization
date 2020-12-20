package com.asiczen.organization.services.organization.svcimpl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface CsvFileServices {

    boolean isCSVFormattedFile(MultipartFile file);

    void uploadOrganizationData(MultipartFile file);
}
