package com.asiczen.organization.services.organization.svcimpl;

import com.asiczen.organization.services.organization.model.ErrorTable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ErrorTableServices {

    public List<ErrorTable> getAllErrorDetailsWithFlag(boolean status);

    public List<ErrorTable> getAllErrorDetails();
}
