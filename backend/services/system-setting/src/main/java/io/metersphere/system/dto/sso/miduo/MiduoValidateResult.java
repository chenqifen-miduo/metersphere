package io.metersphere.system.dto.sso.miduo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MiduoValidateResult implements Serializable {
    private boolean valid;
    private String sessionToken;
    private String weworkUserid;
    private Long expiresAt;
}
