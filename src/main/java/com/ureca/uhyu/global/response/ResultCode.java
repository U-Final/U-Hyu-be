package com.ureca.uhyu.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResultCode {
    /**
     * 1000번대 (글로벌)
     */
    SUCCESS(HttpStatus.OK, 0, "정상 처리 되었습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, 1000, "잘못된 입력값입니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, 1003, "사용자를 찾을 수 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 1006, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, 1007, "권한이 없는 사용자입니다."),

    /**
     * 2000번대 (인증/인가 관련)
     */
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, 2000, "유효하지 않은 토큰입니다."),
    REFRESH_TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, 2001, "Refresh Token이 필요합니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 2002, "Refresh Token이 만료되었습니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 2003, "Access Token이 만료되었습니다."),
    REISSUE_SUCCESS(HttpStatus.OK, 2004, "토큰 재발급에 성공했습니다. "),
    LOGOUT_SUCCESS(HttpStatus.OK, 2005, "로그아웃에 성공했습니다."),
    SIGNUP_SUCCESS(HttpStatus.OK, 2006, "회원가입을 위한 추가정보 입력에 성공했습니다."),
    INVALID_ROLE_IN_TOKEN(HttpStatus.OK, 2007, "token에서 role을 추출할 수 없습니다."),

    /**
     * 3000번대 (매장 관련)
     */
    NOT_FOUND_STORE(HttpStatus.NOT_FOUND, 3001,"매장을 찾을 수 없습니다."),

    /**
     * 4000번대 (유저 관련)
     */
    USER_ONBOARDING_SUCCESS(HttpStatus.CREATED, 4001, "유저 정보가 성공적으로 저장되었습니다."),
    EMAIL_CHECK_SUCCESS(HttpStatus.OK, 4002, "사용 가능한 이메일 입니다."),
    EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, 4003, "이미 사용중인 이메일입니다."),
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, 4004, "즐겨찾기 정보를 찾을 수 없습니다."),
    BOOKMARK_DELETE_SUCCESS(HttpStatus.OK, 4005, "즐겨찾기 삭제가 완료되었습니다."),
    BOOKMARK_LIST_NOT_FOUND(HttpStatus.NOT_FOUND, 4006, "즐겨찾기 리스트가 없습니다."),
    BOOKMARK_LIST_ALREADY_EXISTS(HttpStatus.CONFLICT, 4007, "즐겨찾기 리스트가 이미 존재합니다."),
    MY_MAP_LIST_NOT_FOUND(HttpStatus.NOT_FOUND, 4008, "My Map 리스트가 없습니다."),
    MY_MAP_LIST_DELETE_SUCCESS(HttpStatus.OK, 4009, "My Map 리스트 삭제가 완료되었습니다."),
    NOT_FOUND_RECOMMENDATION_FOR_USER(HttpStatus.NOT_FOUND, 4010, "추천 결과가 존재하지 않습니다"),
    BRAND_ID_IS_NULL(HttpStatus.NOT_FOUND, 4011, "brand id가 존재하지 않습니다."),
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, 4012, "store 정보를 찾을 수 없습니다."),

    /**
     * 4100번대(바코드 관련)
     */
    BARCODE_UPLOAD_SUCCESS(HttpStatus.OK,4102, "바코드 이미지가 저장되었습니다."),
    BARCODE_NOT_FOUND(HttpStatus.NOT_FOUND,4103,"바코드가 존재하지 않습니다"),
    BARCODE_UPDATE_SUCCESS(HttpStatus.OK,4104, "바코드 이미지가 수정되었습니다."),
    S3_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR,4105, "S3 이미지 업로드에 실패했습니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST,4106, "허용되지 않은 파일 확장자입니다."),

    /**
     * 5000번대 (제휴처 관련)
     */
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, 5001, "제휴처 정보를 찾을 수 없습니다."),
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, 5002, "카테고리 정보를 찾을 수 없습니다."),
    RECOMMENDATION_IS_NULL(HttpStatus.NOT_FOUND, 5003, "추천할 브랜드 정보가 없습니다"),
    GRADE_GOOD_NOT_FOUND(HttpStatus.NOT_FOUND,5004,"우수 회원의 혜택 정보가 없습니다."),

    /**
     * 6000번대 (어드민 관련)
     */
    CREATE_BRAND_SUCCESS(HttpStatus.CREATED, 6001, "제휴처 브랜드 정보가 성공적으로 저장되었습니다."),
    UPDATE_BRAND_SUCCESS(HttpStatus.OK, 6002, "제휴처 브랜드 정보가 성공적으로 수정되었습니다."),
    DELETE_BRAND_SUCCESS(HttpStatus.OK, 6003, "제휴처 브랜드 정보가 성공적으로 삭제되었습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, 6005, "관련된 카테고리가 없습니다."),
    BRAND_NAME_DUPLICATED(HttpStatus.BAD_REQUEST, 6006, "브랜드 이름이 중복됩니다."),
    INVALID_STORE_TYPE(HttpStatus.BAD_REQUEST, 6007, "매장 타입은 OFFLINE, ONLINE만 가능합니다."),

    /**
     * 7000번대 (추천 관련)
     */
    RE_RECOMMENDATION_SUCCESS(HttpStatus.OK, 7000,"브랜드 재추천이 성공적으로 완료되었습니다.");

    private final HttpStatus status;
    private final int code;
    private final String message;
}
