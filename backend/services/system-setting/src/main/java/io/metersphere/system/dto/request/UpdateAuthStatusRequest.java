package io.metersphere.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 认证源启用状态更新请求（社区版）
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UpdateAuthStatusRequest implements Serializable {

    @Schema(description = "认证源ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{auth_source.id.not_blank}")
    private String id;

    @Schema(description = "是否启用")
    private Boolean enable;
}
