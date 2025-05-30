package com.cPlace.admin.service;

import com.cPlace.admin.dto.PatchMemberRequest;
import com.cPlace.chzzk.domain.ChzzkMember;
import com.cPlace.fixture.LayerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.cPlace.fixture.Fixtures.BANNED_CHZZK_MEMBER;
import static com.cPlace.fixture.Fixtures.CHZZK_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;

class AdminServiceTest extends LayerTestSupport {

    @Autowired
    private AdminService adminService;

    @Test
    @DisplayName("멤버를 밴할 수 있다.")
    void banMember() {
        // given
        ChzzkMember member = CHZZK_MEMBER;

        // when
        ChzzkMember bannedMember = adminService.changeMemberState(
                PatchMemberRequest.of(member.getChannelName(), true));

        // then
        assertThat(bannedMember.isBanned()).isTrue();
    }

    @Test
    @DisplayName("멤버의 밴을 해제할 수 있다.")
    void unbanMember() {
        // given
        ChzzkMember member = BANNED_CHZZK_MEMBER;

        // when
        ChzzkMember unbannedMember = adminService.changeMemberState(
                PatchMemberRequest.of(member.getChannelName(), false));

        // then
        assertThat(unbannedMember.isBanned()).isFalse();
    }
}
