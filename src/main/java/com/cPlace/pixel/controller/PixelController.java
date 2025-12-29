package com.cPlace.pixel.controller;

import com.cPlace.chzzk.auth.AuthMember;
import com.cPlace.chzzk.domain.ChzzkMember;
import com.cPlace.pixel.domain.Color;
import com.cPlace.pixel.dto.PixelDrawHistoryResponse;
import com.cPlace.pixel.dto.PixelDrawRequest;
import com.cPlace.pixel.service.PixelService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PixelController {

    private final PixelService pixelService;

    @PostMapping("/pixels")
    ResponseEntity<Void> drawPixel(@RequestBody @Valid PixelDrawRequest request, @AuthMember ChzzkMember chzzkMember) {
        pixelService.changePixel(request.x(), request.y(), Color.findColor(request.color()), chzzkMember);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pixels")
    ResponseEntity<Resource> readPixels(@AuthMember ChzzkMember chzzkMember) {
        byte[] pixels = pixelService.readCurrentPixels();
        ByteArrayResource resource = new ByteArrayResource(pixels);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @GetMapping("/dirty-set")
    ResponseEntity<Resource> readPixels(
            @AuthMember ChzzkMember chzzkMember,
            @RequestParam("lastUpdated") long lastUpdated
    ) {
        byte[] pixels = pixelService.readDirtySet(lastUpdated);
        ByteArrayResource resource = new ByteArrayResource(pixels);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @GetMapping("/pixels/history")
    ResponseEntity<List<PixelDrawHistoryResponse>> readLatestDrawHistory(
            @AuthMember ChzzkMember chzzkMember,
            @RequestParam("count") @Positive int count
    ) {
        List<PixelDrawHistoryResponse> histories = pixelService.readUpdateHistory(count);
        return ResponseEntity.ok(histories);
    }

    @GetMapping("/map/history")
    ResponseEntity<Resource> readHistory(
            @AuthMember ChzzkMember chzzkMember
    ) {
        byte[] histories = pixelService.readMapHistory();
        ByteArrayResource resource = new ByteArrayResource(histories);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @GetMapping("/current-user")
    ResponseEntity<Integer> readCurrentUserCount(
            @AuthMember ChzzkMember chzzkMember
    ) {
        Integer count = pixelService.readCurrentUserCount();

        return ResponseEntity.ok(count);
    }
}
