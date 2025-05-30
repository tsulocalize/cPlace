package com.cPlace.chzzk.respository;

import com.cPlace.chzzk.domain.ChzzkMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChzzkMemberRepository extends JpaRepository<ChzzkMember, Long> {

    Optional<ChzzkMember> findByChannelId(String channelId);

    Optional<ChzzkMember> findByChannelName(String channelName);

    Optional<ChzzkMember> findByChannelIdAndAccessToken(String channelId, String accessToken);

    boolean existsByChannelId(String channelId);
}
