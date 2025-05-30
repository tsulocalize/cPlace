package com.cPlace.pixel.domain;

import com.cPlace.chzzk.domain.ChzzkMember;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class PixelUpdateHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "position_x")
    private int positionX;

    @Column(name = "position_y")
    private int positionY;

    @Enumerated(EnumType.STRING)
    private Color color;

    private Long chzzkMemberId;

    private LocalDateTime createdAt;

    private PixelUpdateHistory(int positionX, int positionY, Color color, long chzzkMemberId, LocalDateTime createdAt) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.color = color;
        this.chzzkMemberId = chzzkMemberId;
        this.createdAt = createdAt;
    }

    public PixelUpdateHistory(Pixel pixel, ChzzkMember chzzkMember, LocalDateTime createdAt) {
        this(pixel.x(), pixel.y(), pixel.color(), chzzkMember.getId(), createdAt);
    }
}
