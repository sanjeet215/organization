package com.asiczen.organization.services.organization.services;

import com.asiczen.organization.services.organization.model.ErrorTable;
import com.asiczen.organization.services.organization.repository.ErrorTableRepository;
import com.asiczen.organization.services.organization.svcimpl.ErrorTableServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ErrorTableServicesImpl implements ErrorTableServices {

    @Autowired
    ErrorTableRepository errorTableRepository;

    @Override
    public List<ErrorTable> getAllErrorDetailsWithFlag(boolean status) {
        return errorTableRepository.findAll().stream().filter(record -> record.isStatus() != status).limit(5).collect(Collectors.toList());
    }

    @Override
    public List<ErrorTable> getAllErrorDetails() {
        return errorTableRepository.findAll().stream().limit(5).collect(Collectors.toList());
    }
}
