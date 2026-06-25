package io.metersphere.system.service.impl;

import io.metersphere.system.domain.UserInvite;
import io.metersphere.system.dto.request.UserRegisterRequest;
import io.metersphere.system.dto.user.request.UserBatchCreateRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class CommunityUserXpackServiceImplTest {

    private final CommunityUserXpackServiceImpl service = new CommunityUserXpackServiceImpl();

    @Test
    void gwHowToAddUserBatchReturnsSuccess() {
        Assertions.assertEquals(0, service.GWHowToAddUser(new UserBatchCreateRequest(), "LOCAL", "admin"));
    }

    @Test
    void gwHowToAddUserRegisterReturnsSuccess() throws Exception {
        Assertions.assertEquals(0, service.GWHowToAddUser(new UserRegisterRequest(), new UserInvite()));
    }

    @Test
    void gwHowToChangeUserReturnsSuccess() {
        Assertions.assertEquals(0, service.GWHowToChangeUser(List.of("user1"), true, "admin"));
    }

    @Test
    void gwHowToDeleteUserReturnsSuccess() {
        Assertions.assertEquals(0, service.GWHowToDeleteUser(List.of("user1"), "admin"));
    }
}
