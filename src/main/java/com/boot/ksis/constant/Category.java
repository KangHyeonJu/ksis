package com.boot.ksis.constant;

import com.boot.ksis.entity.API;

public enum Category {
    ACCOUNT_INFO,    // 계정정보
    NOTIFICATION,    // 알림
    MAIN,            // 메인창
    ACCOUNT_LIST,    // 계정목록 조회
    LOG,             // 로그기록
    ORIGINAL,        // 원본
    ENCODED,         // 인코딩
    NOTICE,          // 공지글 관리
    SIGNAGE,         // 디바이스 관리
    PC,              // 일반 PC 관리
    RESOLUTION,      // 해상도
    API,             // API 관리
    FILE_SIZE,       // 용량 관리
    TRASHFILE,       // 휴지통 - 이미지 및 영상
    TRASHNOTICE,     // 휴지통 - 공지

    ///////////////////////////

    LOGIN,           // 로그인
    LOGOUT,          // 로그아웃
    UPLOAD,          // 업로드
    UPLOAD_PROGRESS   // 업로드 진행현황
}
