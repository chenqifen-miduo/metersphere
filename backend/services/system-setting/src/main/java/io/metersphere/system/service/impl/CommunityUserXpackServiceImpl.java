package io.metersphere.system.service.impl;

import io.metersphere.system.domain.UserInvite;
import io.metersphere.system.dto.request.UserRegisterRequest;
import io.metersphere.system.dto.user.request.UserBatchCreateRequest;
import io.metersphere.system.service.UserXpackService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Community edition UserXpackService: no user-count or quota restrictions.
 */
@Service
@ConditionalOnMissingBean(UserXpackService.class)
public class CommunityUserXpackServiceImpl implements UserXpackService {

    private static final int SUCCESS = 0;

    @Override
    public int GWHowToAddUser(UserBatchCreateRequest userCreateDTO, String source, String operator) {
        return SUCCESS;
    }

    @Override
    public int GWHowToAddUser(UserRegisterRequest registerRequest, UserInvite userInvite) {
        return SUCCESS;
    }

    @Override
    public int GWHowToChangeUser(List<String> userIds, boolean enable, String operator) {
        return SUCCESS;
    }

    @Override
    public int GWHowToDeleteUser(List<String> userIdList, String operator) {
        return SUCCESS;
    }
}
