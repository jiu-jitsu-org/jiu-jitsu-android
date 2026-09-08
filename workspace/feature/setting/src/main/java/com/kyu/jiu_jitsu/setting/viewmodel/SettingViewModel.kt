package com.kyu.jiu_jitsu.setting.viewmodel

// 설정 값 조회·변경과 사용자 이벤트 처리를 담당할 ViewModel의 자리다.
// 기능 구현 시 Repository 계약을 주입받고 불변 SettingUiState를 노출한다.
// 단순 조회·변경은 Repository를 직접 호출하고, 복합 비즈니스 로직에만 UseCase를 사용한다.
