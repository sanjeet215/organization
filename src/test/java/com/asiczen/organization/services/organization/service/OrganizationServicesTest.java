package com.asiczen.organization.services.organization.service;

import com.asiczen.organization.services.organization.model.OrgParameters;
import com.asiczen.organization.services.organization.model.Organization;
import com.asiczen.organization.services.organization.repository.OrgParamsRepository;
import com.asiczen.organization.services.organization.repository.OrganizationRepository;
import com.asiczen.organization.services.organization.request.OrganizationOnBoard;
import com.asiczen.organization.services.organization.request.OrganizationUpdateRequest;
import com.asiczen.organization.services.organization.response.OrganizationResponse;
import com.asiczen.organization.services.organization.services.OrganizationServicesImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class OrganizationServicesTest {

    private static final String TOKEN = "somerandomtoken";

    @InjectMocks
    OrganizationServicesImpl organizationServices;

    @Mock
    OrganizationRepository orgRepo;

    @Mock
    OrgParamsRepository paramRepo;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("postNewOrganization test class")
    public void postNewOrganizationTest() {

        OrganizationOnBoard request = new OrganizationOnBoard();
        populateOrganizationOnBoardRequest(request);
        Assertions.assertEquals(new OrganizationResponse("Organization registered successfully and Initial user is created."), organizationServices.postNewOrganization(request, TOKEN));
    }

    @Test
    @DisplayName("Get all Organization test class")
    public void getAllOrganizationsTest() {

        when(orgRepo.findAll()).thenReturn(findAllOrganizationMock());
        Assertions.assertEquals(findallOrganizationExpectedResult(), organizationServices.getAllOrganizations());
    }

    @Test
    @DisplayName("Update Organization test class")
    public void updateOrganizationsTest() {

        OrganizationUpdateRequest request = new OrganizationUpdateRequest(1l, "orgRefName-update", "orgName-update", "org description-update");
        when(orgRepo.save(saveOrganizationMock())).thenReturn(updateOrganizationMock());
        //Assertions.assertEquals(, organizationServices.updateOrganization(request));
    }

    private Organization updateOrganizationMock() {
        return new Organization();
    }

    private void populateOrganizationOnBoardRequest(OrganizationOnBoard request) {
        request.setFirstName("userFirstName");
        request.setLastName("userLastName");
        request.setContactEmail("userEmail@db.com");
        request.setContactNumber("9900643967");
        request.setOrgRefName("ascizen");
        request.setDescription("asiczen pvt ltd.");
        request.setOrgName("Asiczen");
    }


    final Organization saveOrganizationMock() {

        Organization organization = new Organization();
        organization.setOrgRefName("ascizen");
        organization.setDescription("asiczen pvt ltd.");
        organization.setOrgName("Asiczen");

        return organization;
    }

    final void saveOrganizationParameterMock() {

        Organization organization = new Organization();
        organization.setOrgRefName("asiczen");
        organization.setDescription("asiczen pvt ltd.");
        organization.setOrgName("Asiczen");
        OrgParameters params = new OrgParameters();
        params.setOrganization(organization);
        when(paramRepo.save(params)).thenReturn(params);
    }

    final List<Organization> findAllOrganizationMock() {

        Organization organization = new Organization();
        organization.setOrgRefName("asiczen-1");
        organization.setDescription("asiczen pvt ltd.-1");
        organization.setOrgName("Asiczen-1");

        Organization organization2 = new Organization();
        organization2.setOrgRefName("asiczen-2");
        organization2.setDescription("asiczen pvt ltd.-2");
        organization2.setOrgName("Asiczen-2");

        List<Organization> organizations = new ArrayList<>();
        organizations.add(organization);
        organizations.add(organization2);

        return organizations;
    }


    private List<Organization> findallOrganizationExpectedResult() {
        Organization organization = new Organization();
        organization.setOrgRefName("asiczen-1");
        organization.setDescription("asiczen pvt ltd.-1");
        organization.setOrgName("Asiczen-1");
        organization.setCreatedAt(new Date());
        organization.setUpdatedAt(new Date());

        Organization organization2 = new Organization();
        organization2.setOrgRefName("asiczen-2");
        organization2.setDescription("asiczen pvt ltd.-2");
        organization2.setOrgName("Asiczen-2");
        organization2.setCreatedAt(new Date());
        organization2.setUpdatedAt(new Date());

        List<Organization> organizations = new ArrayList<>();
        organizations.add(organization);
        organizations.add(organization2);

        return organizations;
    }
}
