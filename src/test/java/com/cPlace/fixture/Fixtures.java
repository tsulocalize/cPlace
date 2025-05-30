package com.cPlace.fixture;

import com.cPlace.chzzk.domain.ChzzkMember;
import com.cPlace.chzzk.respository.ChzzkMemberRepository;

public class Fixtures {

    public static ChzzkMember CHZZK_MEMBER;
    public static ChzzkMember BANNED_CHZZK_MEMBER;
    public static ChzzkMember CHZZK_ADMIN;

    public static void saveMembers(ChzzkMemberRepository chzzkMemberRepository) {
        CHZZK_MEMBER = new ChzzkMember(
                "CH_ID",
                "CH_NAME",
                "ACCESS_TOKEN",
                "REFRESH_TOKEN");
        chzzkMemberRepository.save(CHZZK_MEMBER);


        BANNED_CHZZK_MEMBER = new ChzzkMember(
                "CH_ID2",
                "CH_NAME2",
                "ACCESS_TOKEN2",
                "REFRESH_TOKEN2");
        BANNED_CHZZK_MEMBER.ban();
        chzzkMemberRepository.save(BANNED_CHZZK_MEMBER);

        CHZZK_ADMIN = new ChzzkMember(
                "CH_ID3",
                "CH_NAME3",
                "ACCESS_TOKEN3",
                "REFRESH_TOKEN3",
                true);
        chzzkMemberRepository.save(CHZZK_ADMIN);
    }
}
