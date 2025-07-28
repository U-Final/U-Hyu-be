package com.ureca.uhyu.domain.user.controller;

import com.ureca.uhyu.domain.auth.dto.UserEmailCheckRequest;
import com.ureca.uhyu.domain.user.dto.request.ActionLogsReq;
import com.ureca.uhyu.domain.user.dto.request.SaveRecentVisitReq;
import com.ureca.uhyu.domain.user.dto.request.UpdateUserReq;
import com.ureca.uhyu.domain.user.dto.request.UserOnboardingReq;
import com.ureca.uhyu.domain.user.dto.response.*;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.global.annotation.CurrentUser;
import com.ureca.uhyu.global.response.CommonResponse;
import com.ureca.uhyu.global.response.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface UserControllerDocs {

    @Operation(
            summary = "신규 유저 온보딩 정보 저장",
            description = """
                    신규 유저의 추가 입력 정보를 저장하고 권한을 변경하며 토큰을 재발급합니다.
                    
                    **처리 과정:**
                    1. 사용자 온보딩 정보 저장 (등급, 최근 이용 브랜드, 관심 브랜드)
                    2. 사용자 권한을 TMP_USER에서 USER로 변경
                    3. 새로운 Access Token 발급 (쿠키에 저장)
                    4. Refresh Token 생성
                    
                    **인증 필요:** JWT Bearer Token (TMP_USER 권한)
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "온보딩 정보 저장 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "온보딩 성공 예시",
                                    value = """
                                            {
                                              "statusCode": 0,
                                              "message": "유저 정보가 성공적으로 저장되었습니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            )
    })
    public CommonResponse<ResultCode> onboarding(
            @Parameter(
                    description = "온보딩 정보",
                    required = true
            ) @Valid @RequestBody UserOnboardingReq request,
            @Parameter(hidden = true) HttpServletResponse response,
            @Parameter(
                    description = "현재 로그인된 사용자 정보",
                    hidden = true
            ) @CurrentUser User user
    );


    @Operation(
            summary = "개인정보 조회",
            description = "현재 로그인한 사용자의 개인정보를 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "개인정보 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "개인정보 조회 성공 예시",
                                    value = """
                                            {
                                              "statusCode": 0,
                                              "message": "정상 처리 되었습니다.",
                                              "data": {
                                                "profileImage": "https://example.com/profile.jpg",
                                                "userName": "홍길동",
                                                "nickName": "길동이",
                                                "email": "hong@example.com",
                                                "age": 25,
                                                "gender": "MALE",
                                                "grade": "VIP",
                                                "updatedAt": "2024-01-15T10:30:00"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            )
    })
    @GetMapping
    public CommonResponse<GetUserInfoRes> getByUser(
            @Parameter(
                    description = "현재 로그인된 사용자 정보",
                    hidden = true
            ) @CurrentUser User user);



    @Operation(
            summary = "개인정보 수정",
            description = "사용자의 개인정보를 수정합니다. 요청한 값들만 수정됩니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "개인정보 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            )
    })
    @PatchMapping
    public CommonResponse<UpdateUserRes> updateByUser(
            @Parameter(
                    description = "현재 로그인된 사용자 정보",
                    hidden = true
            ) @CurrentUser User user,
            @Parameter(
                    description = "수정할 사용자 정보",
                    required = true
            ) @RequestBody UpdateUserReq request
    );


    @Operation(
            summary = "이메일 중복 확인",
            description = "신규 유저가 입력한 이메일의 중복 여부를 확인합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "이메일 사용 가능",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "이메일 사용 가능 예시",
                                    value = """
                                            {
                                              "statusCode": 4002,
                                              "message": "사용 가능한 이메일 입니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이메일 중복",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "이메일 중복 예시",
                                    value = """
                                            {
                                              "statusCode": 4003,
                                              "message": "이미 사용중인 이메일입니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            )
    })
    @PostMapping("/check-email")
    public CommonResponse<ResultCode> checkEmailDuplicate(
            @Parameter(
                    description = "확인할 이메일 정보",
                    required = true
            ) @RequestBody UserEmailCheckRequest request,
            @Parameter(
                    description = "현재 로그인된 사용자 정보",
                    hidden = true
            ) @CurrentUser User user);



    @Operation(
            summary = "즐겨찾기 조회",
            description = "사용자가 등록한 즐겨찾기 목록을 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "즐겨찾기 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            )
    })
    @GetMapping("/bookmark")
    public CommonResponse<List<BookmarkRes>> getBookmarkList(
            @Parameter(
                    description = "현재 로그인된 사용자 정보",
                    hidden = true
            ) @CurrentUser User user);



    @Operation(
            summary = "즐겨찾기 삭제",
            description = "지정한 즐겨찾기를 삭제합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "즐겨찾기 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "즐겨찾기 삭제 성공 예시",
                                    value = """
                                            {
                                              "statusCode": 0,
                                              "message": "즐겨찾기 삭제가 완료되었습니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "즐겨찾기를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            )
    })
    @DeleteMapping("/bookmark/{bookmark_id}")
    public CommonResponse<ResultCode> deleteBookmark(
            @Parameter(
                    description = "현재 로그인된 사용자 정보",
                    hidden = true
            ) @CurrentUser User user,
            @Parameter(
                    description = "삭제할 즐겨찾기 ID",
                    example = "1"
            ) @PathVariable Long bookmark_id);



    @Operation(
            summary = "사용자 활동내역 조회",
            description = """
                    사용자의 활동 통계 정보를 조회합니다.
                    
                    **포함 정보:**
                    - 자주 조회한 브랜드
                    - 이번 달 받은 혜택
                    - 최근 방문 매장 (최대 3곳)
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "활동내역 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            )
    })
    @GetMapping("/statistics")
    public CommonResponse<UserStatisticsRes> getUserStatistics(
            @Parameter(
                    description = "현재 로그인된 사용자 정보",
                    hidden = true
            ) @CurrentUser User user);

    @Operation(
            summary = "유저 action logs 저장",
            description = "유저가 클릭한 마커 정보를 기반으로 매장 id, 카테고리 필터링 클릭 정보를 기반으로 카테고리 id에 대한 action logs를 저장"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    CommonResponse<SaveUserInfoRes> actionLogs(
            @Parameter(hidden = true) User user,
            @Parameter(description = "액션 로그 요청 데이터") @Valid @RequestBody ActionLogsReq request
    );

    @Operation(
            summary = "유저 방문 브랜드 저장",
            description = """
                    사용자가 특정 매장을 방문했다고 체크했을 때,
                    해당 매장의 브랜드를 찾아 RecommendationBaseData에 저장합니다.

                    **처리 내용:**
                    - 매장 ID를 기반으로 브랜드를 조회
                    - DataType.RECENT으로 저장

                    **인증 필요:** JWT Bearer Token
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "방문 브랜드 저장 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "방문 브랜드 저장 성공 예시",
                                    value = """
                                            {
                                              "statusCode": 0,
                                              "message": "정상 처리 되었습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 매장을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            )
    })
    @PostMapping("/visited")
    CommonResponse<SaveUserInfoRes> visited(
            @Parameter(hidden = true) @CurrentUser User user,
            @Parameter(description = "방문한 매장 ID 요청") @Valid @RequestBody SaveRecentVisitReq request
    );
}
