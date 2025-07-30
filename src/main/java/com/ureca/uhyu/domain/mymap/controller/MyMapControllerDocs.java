package com.ureca.uhyu.domain.mymap.controller;

import com.ureca.uhyu.domain.mymap.dto.request.CreateMyMapListReq;
import com.ureca.uhyu.domain.mymap.dto.request.UpdateMyMapListReq;
import com.ureca.uhyu.domain.mymap.dto.response.*;
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
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface MyMapControllerDocs {
    @Operation(
            summary = "My Map List 조회",
            description = """
                    사용자가 생성한 My Map 목록을 조회합니다.
                    
                    **용도:** 어느 My Map에 저장할지 정하기 위해 목록 조회
                    **참고:** 즐겨찾기는 프론트엔드에서 버튼만 구현
                    
                    **인증 필요:** JWT Bearer Token
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "My Map 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "My Map 목록 조회 성공 예시",
                                    value = """
                                            {
                                              "statusCode": 0,
                                              "message": "정상 처리 되었습니다.",
                                              "data": [
                                                {
                                                  "myMapListId": 1,
                                                  "title": "영화관 투어",
                                                  "markerColor": "RED",
                                                  "uuid": "550e8400-e29b-41d4-a716-446655440000",
                                                  "storeCount": 5
                                                }
                                              ]
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
    public CommonResponse<List<MyMapListRes>> getMyMapList(
            @Parameter(
            description = "현재 로그인된 사용자 정보",
            hidden = true
            ) @CurrentUser User user
    );

    @Operation(
            summary = "My Map List 추가",
            description = """
                    사용자가 새로운 My Map을 생성합니다.
                    
                    **필수 정보:**
                    - **title**: 지도 제목 (최대 20자)
                    - **markerColor**: 마커 색상 (RED, ORANGE, YELLOW, GREEN, PURPLE)
                    - **uuid**: 고유 식별자 (UUID 형식)
                    
                    **인증 필요:** JWT Bearer Token
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "My Map 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "My Map 생성 성공 예시",
                                    value = """
                                            {
                                              "statusCode": 0,
                                              "message": "정상 처리 되었습니다.",
                                              "data": {
                                                "myMapListId": 1,
                                                "title": "영화관 투어",
                                                "markerColor": "RED",
                                                "uuid": "550e8400-e29b-41d4-a716-446655440000"
                                              }
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
    public CommonResponse<CreateMyMapListRes> createMyMapList(
            @Parameter(
                    description = "현재 로그인된 사용자 정보",
                    hidden = true
            ) @CurrentUser User user,
            @Parameter(
                    description = "생성할 My Map 정보",
                    required = true
            ) @Valid @RequestBody CreateMyMapListReq createMyMapListReq
    );

    @Operation(
            summary = "My Map List 수정",
            description = """
                    My Map의 제목 또는 마커 색상을 수정합니다.
                    
                    **수정 가능 항목:**
                    - **title**: 지도 제목 (최대 20자)
                    - **markerColor**: 마커 색상
                    
                    **인증 필요:** JWT Bearer Token
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "My Map 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "My Map 수정 성공 예시",
                                    value = """
                                            {
                                              "statusCode": 0,
                                              "message": "정상 처리 되었습니다.",
                                              "data": {
                                                "myMapListId": 1,
                                                "title": "영화관 투어 수정",
                                                "markerColor": "GREEN"
                                              }
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
                    responseCode = "404",
                    description = "My Map을 찾을 수 없음",
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
    public CommonResponse<UpdateMyMapListRes> updateMyMapList(
            @Parameter(
                    description = "현재 로그인된 사용자 정보",
                    hidden = true
            ) @CurrentUser User user,
            @Parameter(
                    description = "수정할 My Map 정보",
                    required = true
            ) @Valid @RequestBody UpdateMyMapListReq updateMyMapListReq
    );

    @Operation(
            summary = "My Map List 삭제",
            description = "지정한 My Map List를 삭제합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "My Map 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "My Map 삭제 성공 예시",
                                    value = """
                                            {
                                              "statusCode": 4008,
                                              "message": "My Map 리스트 삭제가 완료되었습니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "My Map을 찾을 수 없음",
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
    public CommonResponse<ResultCode> deleteMyMapList(
            @Parameter(
                    description = "현재 로그인된 사용자 정보",
                    hidden = true
            ) @CurrentUser User user,
            @Parameter(
                    description = "삭제할 My Map List ID",
                    example = "1"
            ) @PathVariable Long myMapListId
    );

    @Operation(
            summary = "UUID 기반 My Map 지도 조회(회원)",
            description = """
                    UUID를 기반으로 특정 My Map의 상세 정보를 조회합니다.
                    
                    **포함 정보:**
                    - 지도 기본 정보 (제목, 마커 색상)
                    - 등록된 매장 목록
                    - 각 매장의 위치 및 정보
                    
                    **인증 필요:** JWT Bearer Token
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "My Map 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "My Map 조회 성공 예시",
                                    value = """
                                            {
                                              "statusCode": 0,
                                              "message": "정상 처리 되었습니다.",
                                              "data": {
                                                "myMapListId": 1,
                                                "title": "영화관 투어",
                                                "markerColor": "RED",
                                                "uuid": "550e8400-e29b-41d4-a716-446655440000",
                                                "stores": [
                                                  {
                                                    "storeId": 1,
                                                    "storeName": "CGV 동대문",
                                                    "address": "서울특별시 중구 장충단로13길 20",
                                                    "latitude": 37.5687346,
                                                    "longitude": 127.0076665
                                                  }
                                                ]
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "My Map을 찾을 수 없음",
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
    public CommonResponse<MyMapRes> getMyMapByUUID(
            @Parameter(
                    description = "현재 로그인된 사용자 정보",
                    hidden = true
            ) @CurrentUser User user,
            @Parameter(
                    description = "My Map UUID",
                    example = "550e8400-e29b-41d4-a716-446655440000"
            ) @PathVariable String uuid
    );

    @Operation(
            summary = "UUID 기반 My Map 조회(비회원)",
            description = """
                    비회원이 UUID를 통해 공유된 My Map의 상세 정보를 조회합니다.
                 
                   **특징:**
                    - 인증 불필요 (토큰 없이 접근 가능)
                    - 회원용 조회와 동일한 데이터 반환
                    - 공유 목적으로 사용됨
                   
                    **인증 불필요:** 별도 토큰 없이 접근 가능
                   """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "My Map 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "비회원 My Map 조회 성공 예시",
                                    value = """
                                           {
                                              "statusCode": 0,
                                              "message": "정상 처리 되었습니다.",
                                              "data": {
                                                "myMapListId": 1,
                                                "title": "영화관 투어",
                                                "markerColor": "RED",
                                                "uuid": "550e8400-e29b-41d4-a716-446655440000",
                                                "isMine": false,
                                                "stores": [
                                                  {
                                                    "storeId": 1,
                                                    "storeName": "CGV 동대문",
                                                    "address": "서울특별시 중구 장충단로13길 20",
                                                    "latitude": 37.5687346,
                                                    "longitude": 127.0076665
                                                  }
                                                ]
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "My Map을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            )
    })
    public CommonResponse<MyMapRes> getMyMapByUUIDWithGuest(
            @Parameter(
                    description = "My Map UUID",
                    example = "550e8400-e29b-41d4-a716-446655440000"
            ) @PathVariable String uuid
    );

    @Operation(
            summary = "My Map 매장 등록 유무 조회",
            description = """
                    특정 매장이 사용자의 My Map들에 등록되어 있는지 확인합니다.
                    
                    **용도:** 매장을 My Map에 추가할 때 중복 등록 방지
                    **반환:** 각 My Map별 등록 상태 정보
                    
                    **인증 필요:** JWT Bearer Token
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "매장 등록 유무 조회 성공",
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
    public CommonResponse<BookmarkedMyMapRes> getMyMapListWithIsBookmarked(
            @Parameter(
                    description = "현재 로그인된 사용자 정보",
                    hidden = true
            ) @CurrentUser User user,
            @Parameter(
                    description = "조회할 매장 ID",
                    example = "1"
            ) @PathVariable(name = "store_id") Long storeId
    );

    @Operation(
            summary = "매장 My Map 토글",
            description = """
                    특정 매장을 My Map에서 추가하거나 제거합니다.
                    
                    **동작:** 등록되어 있으면 제거, 등록되어 있지 않으면 추가
                    **용도:** 매장 상세 페이지의 즐겨찾기 토글 기능
                    
                    **인증 필요:** JWT Bearer Token
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "매장 My Map 토글 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "My Map 또는 매장을 찾을 수 없음",
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
    public CommonResponse<ToggleMyMapRes> toggleMyMap(
            @Parameter(
                    description = "현재 로그인된 사용자 정보",
                    hidden = true
            ) @CurrentUser User user,
            @Parameter(
                    description = "대상 My Map List ID",
                    example = "1"
            ) @PathVariable Long myMapListId,
            @Parameter(
                    description = "토글할 매장 ID",
                    example = "1"
            ) @PathVariable(name = "store_id") Long storeId
    );
}
