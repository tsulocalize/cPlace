package com.cPlace.admin.controller;

import com.cPlace.admin.dto.CoverPixelRequest;
import com.cPlace.admin.dto.PatchMemberRequest;
import com.cPlace.admin.service.AdminService;
import com.cPlace.chzzk.auth.AuthAdmin;
import com.cPlace.chzzk.domain.ChzzkMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PatchMapping("/admin/member")
    public ResponseEntity<Void> patchUser(
            @AuthAdmin ChzzkMember admin,
            @RequestBody PatchMemberRequest request
    ) {
        adminService.changeMemberState(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/pixel")
    @Validated
    public ResponseEntity<Void> coverPixel(
            @AuthAdmin ChzzkMember admin,
            @RequestBody CoverPixelRequest request
    ) {
        adminService.coverPixel(request);
        return ResponseEntity.noContent().build();
    }
}
