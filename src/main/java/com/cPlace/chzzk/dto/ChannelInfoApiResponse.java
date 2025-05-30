package com.cPlace.chzzk.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class ChannelInfoApiResponse {

    private final String channelId;
    private final String channelName;
}
