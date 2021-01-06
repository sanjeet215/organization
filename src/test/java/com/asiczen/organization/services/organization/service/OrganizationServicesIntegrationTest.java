package com.asiczen.organization.services.organization.service;

import com.asiczen.organization.services.organization.exception.ResourceAlreadyExistException;
import com.asiczen.organization.services.organization.model.Organization;
import com.asiczen.organization.services.organization.request.OrganizationOnBoard;
import com.asiczen.organization.services.organization.request.OrganizationUpdateRequest;
import com.asiczen.organization.services.organization.response.OrganizationResponse;
import com.asiczen.organization.services.organization.svcimpl.OrganizationServices;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class OrganizationServicesIntegrationTest {

    private static final String TOKEN = "somerandomtoken";

    @Autowired
    private OrganizationServices organizationServices;

    // Create Organization is base method and should be executed before any other method else other test cases will fail.

    @BeforeAll
    public void populateOrganization() {
        organizationServices.postNewOrganization(getOrganizationOnBoardRequest(), TOKEN);
    }

    @Test
    @DisplayName("Create organization/ Get Organization by orgrefname service integration test")
    @Order(1)
    public void createOrganization() {

        log.info(">>>>>>>>>> Starting create organization test. <<<<<<<<<< ");
        Assertions.assertEquals(new OrganizationResponse("Organization registered successfully and Initial user is created."), organizationServices.postNewOrganization(createOrganizationOnBoardRequest(), TOKEN));

        Organization organization = organizationServices.getOrgByRefName("asiczen4");

        OrganizationOnBoard request = createOrganizationOnBoardRequest();

        Assertions.assertEquals(request.getOrgRefName(), organization.getOrgRefName());
        Assertions.assertEquals(request.getOrgName(), organization.getOrgName());
        Assertions.assertEquals(request.getDescription(), organization.getDescription());
        Assertions.assertEquals(2L, organization.getOrgId());

        log.info(">>>>>>>>>> Finishing create organization test. <<<<<<<<<< ");
    }

    @Test
    @DisplayName("Update Organization update integration test")
    @Order(2)
    public void updateOrganization() {

        log.info(">>>>>>>>>> Starting  update organization <<<<<<<<<< ");

        OrganizationUpdateRequest updateRequest = updateOrganizationRequest();
        organizationServices.updateOrganization(updateRequest);
        Organization organization = organizationServices.getOrgByRefName("asiczen");

        Assertions.assertEquals(updateRequest.getOrgRefName(), organization.getOrgRefName());
        Assertions.assertEquals(updateRequest.getOrgName(), organization.getOrgName());
        Assertions.assertEquals(updateRequest.getDescription(), organization.getDescription());
        Assertions.assertEquals(1L, organization.getOrgId());

        log.info(">>>>>>>>>> Finished  update organization <<<<<<<<<< ");
    }

    @Test
    @DisplayName("Duplicate Record insertion testing")
    @Order(3)
    public void duplicateInsertion() {
        log.info(">>>>>>>>>> Starting  duplicate organization insertion test <<<<<<<<<< ");
        Assertions.assertThrows(ResourceAlreadyExistException.class, () -> organizationServices.postNewOrganization(getOrganizationOnBoardRequest(), TOKEN));
        log.info(">>>>>>>>>> Finishing  duplicate organization insertion test <<<<<<<<<< ");
    }


    @Test
    @DisplayName("Get all organization and sorting the results.")
    @Order(4)
    public void getAllOrganizationsTest() {
        log.info(">>>>>>>>>> Starting  get all organization testing. <<<<<<<<<< ");

        OrganizationOnBoard request = new OrganizationOnBoard();

        request.setFirstName("userFirstName");
        request.setLastName("userLastName");
        request.setContactEmail("userEmail@db.com");
        request.setContactNumber("9900643967");
        request.setOrgRefName("asiczen2");
        request.setDescription("asiczen pvt ltd.2");
        request.setOrgName("asiczen");
        organizationServices.postNewOrganization(request, TOKEN);

        List<Organization> organizationList = organizationServices.getAllOrganizations();

        Assertions.assertEquals(3, organizationList.size());
        Assertions.assertEquals(1l, organizationList.get(0).getOrgId());
        Assertions.assertEquals("asiczen4", organizationList.get(1).getOrgName());

        log.info(">>>>>>>>>> Starting  get all organization testing. <<<<<<<<<< ");
    }

    @Test
    @DisplayName("Get organization count.")
    @Order(5)
    public void getOrganizationCountTest() {
        log.debug(">>>>>>>>>> Starting  organization count test. <<<<<<<<<< ");
        Assertions.assertEquals(3, organizationServices.getOrganizationCount());
        log.debug(">>>>>>>>>> Finishing  organization count test. <<<<<<<<<< ");
    }

    @Test
    @DisplayName("Get organization by ID.")
    @Order(6)
    public void getOrganizationByIdTest() {

        Organization result = organizationServices.getOrganizationById(1l);
        Assertions.assertEquals(1l, result.getOrgId());
        Assertions.assertEquals("asiczen", result.getOrgRefName().trim());

    }

    @Test
    @DisplayName("Enable/Disable Organization")
    @Order(7)
    public void disableOrganization() {

        organizationServices.disableOrganization(1l, false);
        Assertions.assertEquals(false, organizationServices.getOrganizationById(1l).isStatus());

        organizationServices.disableOrganization(1l, true);
        Assertions.assertEquals(true, organizationServices.getOrganizationById(1l).isStatus());
    }

    @Test
    @DisplayName("Validate organization reference Name")
    @Order(8)
    public void validateOrganizationReferenceNameTest() {

        Assertions.assertThrows(ResourceAlreadyExistException.class, () -> organizationServices.validateOrganizationReferenceName("asiczen"));
        Assertions.assertEquals("Org reference name is available.", organizationServices.validateOrganizationReferenceName("asiczen111"));
    }

    @Test
    @DisplayName("Delete organization reference Name")
    @Order(9)
    @Transactional
    public void deleteOrganizationByOrgId() {

        OrganizationOnBoard request = new OrganizationOnBoard();

        request.setFirstName("userFirstName");
        request.setLastName("userLastName");
        request.setContactEmail("userEmail@db.com");
        request.setContactNumber("9900643967");
        request.setOrgRefName("asiczen77");
        request.setDescription("asiczen pvt ltd.77");
        request.setOrgName("asiczen77");
        organizationServices.postNewOrganization(request, TOKEN);

        List<Organization> organizationList = organizationServices.getAllOrganizations();
        Organization organization = organizationList.stream().filter(record -> record.getOrgRefName().equalsIgnoreCase("asiczen77")).findFirst().get();

        Assertions.assertEquals(4, organizationList.size());
        organizationServices.deleteOrganizationByOrgId(organization.getOrgId());
        organizationList = organizationServices.getAllOrganizations();
        //Assertions.assertEquals(3, organizationList.size());


    }


    private OrganizationUpdateRequest updateOrganizationRequest() {
        return new OrganizationUpdateRequest(1l, "asiczen", "asiczen-update", "asiczen pvt ltd-update");
    }

    private OrganizationOnBoard getOrganizationOnBoardRequest() {

        OrganizationOnBoard request = new OrganizationOnBoard();

        request.setFirstName("userFirstName");
        request.setLastName("userLastName");
        request.setContactEmail("userEmail@db.com");
        request.setContactNumber("9900643967");
        request.setOrgRefName("asiczen");
        request.setDescription("asiczen pvt ltd.");
        request.setOrgName("asiczen");

        return request;
    }

    private OrganizationOnBoard createOrganizationOnBoardRequest() {

        OrganizationOnBoard request = new OrganizationOnBoard();

        request.setFirstName("userFirstName");
        request.setLastName("userLastName");
        request.setContactEmail("userEmail@db.com");
        request.setContactNumber("9900643967");
        request.setOrgRefName("asiczen4");
        request.setDescription("asiczen pvt ltd4.");
        request.setOrgName("asiczen4");

        return request;
    }
}
