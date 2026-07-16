package io.metersphere.system.dto.sso.miduo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MiduoSsoCallbackRequest implements Serializable {
    private String token;
    private String state;
}
