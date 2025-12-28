package com.cPlace.chzzk.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "chzzk_members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class ChzzkMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String channelId;

    private String channelName;

    private String accessToken;

    private String refreshToken;

    private LocalDateTime lastActiveAt;

    private boolean banned;

    private boolean isAdmin;

    @CreatedDate
    private LocalDateTime createdAt;

    public ChzzkMember(String channelId, String channelName, String accessToken, String refreshToken) {
        this(channelId, channelName, accessToken, refreshToken, false);
    }

    public ChzzkMember(String channelId, String channelName, String accessToken, String refreshToken, boolean isAdmin) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.banned = false;
        this.isAdmin = isAdmin;
    }

    public boolean isAccessTokenValid(String accessToken) {
        return this.accessToken.equals(accessToken);
    }

    public void updateToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public void updateLastActiveAt(LocalDateTime lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    public void ban() {
        this.banned = true;
    }

    public void unban() {
        this.banned = false;
    }
}


