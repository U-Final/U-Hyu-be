package com.ureca.uhyu.domain.user.controller;

import com.ureca.uhyu.domain.user.dto.response.GetUserInfoRes;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "개인정보 조회", description = "개인정보 조회: 로그인 필요")
    @GetMapping
    public ResponseEntity<GetUserInfoRes> getByUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.findByUser(user));
    }
}
