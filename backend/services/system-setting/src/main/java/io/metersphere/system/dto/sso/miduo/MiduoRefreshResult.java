package io.metersphere.system.dto.sso.miduo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MiduoRefreshResult implements Serializable {
    private boolean success;
    private String sessionToken;
    private Long expiresAt;
}
