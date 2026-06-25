package io.metersphere.system.service.impl;

import io.metersphere.system.dto.sdk.LicenseDTO;
import io.metersphere.system.service.LicenseService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

/**
 * Community edition LicenseService: always reports a valid license.
 */
@Service
@ConditionalOnMissingBean(LicenseService.class)
public class CommunityLicenseServiceImpl implements LicenseService {

    private static LicenseDTO validLicense() {
        LicenseDTO licenseDTO = new LicenseDTO();
        licenseDTO.setStatus("valid");
        return licenseDTO;
    }

    @Override
    public LicenseDTO refreshLicense() {
        return validLicense();
    }

    @Override
    public LicenseDTO validate() {
        return validLicense();
    }

    @Override
    public LicenseDTO addLicense(String licenseCode, String userId) {
        return validLicense();
    }

    @Override
    public String getCode(String encrypt) {
        return encrypt;
    }
}
