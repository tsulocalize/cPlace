package com.cPlace.admin.dto;

public record PatchMemberRequest(String channelName, boolean ban) {

    public static PatchMemberRequest of(String channelName, boolean ban) {
        return new PatchMemberRequest(channelName, ban);
    }
}
