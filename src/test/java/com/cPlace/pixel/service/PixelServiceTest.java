package com.cPlace.pixel.service;

import com.cPlace.chzzk.respository.ChzzkMemberRepository;
import com.cPlace.fixture.LayerTestSupport;
import com.cPlace.pixel.domain.Color;
import com.cPlace.pixel.domain.PixelUpdateHistory;
import com.cPlace.pixel.exception.PixelException;
import com.cPlace.pixel.exception.PixelExceptionCode;
import com.cPlace.pixel.repository.PixelUpdateHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.cPlace.fixture.Fixtures.BANNED_CHZZK_MEMBER;
import static com.cPlace.fixture.Fixtures.CHZZK_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class PixelServiceTest extends LayerTestSupport {

    @Autowired
    PixelService pixelService;
    @Autowired
    PixelUpdateHistoryRepository pixelUpdateHistoryRepository;
    @Autowired
    ChzzkMemberRepository chzzkMemberRepository;

    @Test
    @DisplayName("픽셀 하나를 변경할 수 있다")
    void changePixel() {
        pixelService.changePixel(0, 0, Color.RED, CHZZK_MEMBER);
    }

    @Test
    @DisplayName("해당 위치에 픽셀이 없으면 픽셀을 변경할 수 없다")
    void changePixel_notFound() {
        // when
        assertThatThrownBy(() -> pixelService.changePixel(-1, -1, Color.RED, CHZZK_MEMBER))
                .isInstanceOf(PixelException.class)
                .hasMessage(PixelExceptionCode.PIXEL_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("픽셀 변경 정보는 기록된다")
    void changePixel_history() {
        // when
        pixelService.changePixel(0, 0, Color.RED, CHZZK_MEMBER);

        // then
        PixelUpdateHistory history = pixelUpdateHistoryRepository.findById(1L).get();
        assertAll(() -> {
            assertThat(history.getPositionX()).isEqualTo(0);
            assertThat(history.getPositionY()).isEqualTo(0);
            assertThat(history.getColor()).isEqualTo(Color.RED);
            assertThat(history.getChzzkMemberId()).isEqualTo(CHZZK_MEMBER.getId());
        });
    }

    @Test
    @DisplayName("밴 당한 멤버는 픽셀을 업데이트 할 수 없다")
    void changePixel_unAuthorized() {
        // when && then
        assertThatThrownBy(() -> pixelService.changePixel(0, 0, Color.RED, BANNED_CHZZK_MEMBER))
                .isInstanceOf(PixelException.class)
                .hasMessage(PixelExceptionCode.AUTHORIZATION_INVALID.getMessage());
    }

    @Test
    @DisplayName("시간 제한이 지나지 않으면 픽셀을 업데이트 할 수 없다")
    void changePixel_timeLimited() {
        // when
        pixelService.changePixel(0, 0, Color.RED, CHZZK_MEMBER);

        // then
        assertThatThrownBy(() -> pixelService.changePixel(0, 0, Color.BLUE, CHZZK_MEMBER))
                .isInstanceOf(PixelException.class)
                .hasMessage(PixelExceptionCode.TIME_LIMITED.getMessage());
    }
}
