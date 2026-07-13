package io.metersphere.system.service.impl;

import io.metersphere.system.dto.sdk.LicenseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CommunityLicenseServiceImplTest {

    private final CommunityLicenseServiceImpl service = new CommunityLicenseServiceImpl();

    @Test
    void validateReturnsValid() {
        LicenseDTO licenseDTO = service.validate();
        Assertions.assertNotNull(licenseDTO);
        Assertions.assertEquals("valid", licenseDTO.getStatus());
    }

    @Test
    void refreshLicenseReturnsValid() {
        LicenseDTO licenseDTO = service.refreshLicense();
        Assertions.assertEquals("valid", licenseDTO.getStatus());
    }

    @Test
    void addLicenseReturnsValid() {
        LicenseDTO licenseDTO = service.addLicense("test-license", "admin");
        Assertions.assertEquals("valid", licenseDTO.getStatus());
    }

    @Test
    void getCodeReturnsInput() {
        Assertions.assertEquals("encrypted-code", service.getCode("encrypted-code"));
    }
}
